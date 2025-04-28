package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.config.HubspotProperties;
import br.com.meetime.hubspotintegrator.dto.response.TokenResponseDto;
import br.com.meetime.hubspotintegrator.exception.HubSpotApiException;
import br.com.meetime.hubspotintegrator.exception.HubSpotAuthorizationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
        import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final HubspotProperties hubspotProperties;
    private final WebClient webClient;

    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();
    private final Map<String, String> accessTokenCache = new ConcurrentHashMap<>();
    private final Map<String, Instant> accessTokenExpirationCache = new ConcurrentHashMap<>();

    public String generateAuthorizationUrl() {
        String scopes = hubspotProperties.getScope();
        String encodedScopes = URLEncoder.encode(scopes, StandardCharsets.UTF_8);

        return UriComponentsBuilder
                .fromUriString(hubspotProperties.getAuthorizationUrl())
                .queryParam("client_id", hubspotProperties.getClientId())
                .queryParam("redirect_uri", hubspotProperties.getRedirectUri())
                .queryParam("scope", encodedScopes)
                .queryParam("response_type", "code")
                .build()
                .toUriString();
    }

    public void exchangeCodeForToken(String code, HttpSession session) {
        String body = buildAuthorizationCodeBody(code);
        String response = sendTokenRequest(body);
        parseAndStoreTokens(response, session);
    }

    public TokenResponseDto refreshAccessToken(HttpSession session) {
        String refresh = refreshTokenStore.get(session.getId());
        if (refresh == null) {
            throw new HubSpotAuthorizationException("Refresh token não encontrado para esta sessão.");
        }

        String body = buildRefreshTokenBody(refresh);
        String response = sendTokenRequest(body);
        return parseAndStoreTokens(response, session);
    }

    private String buildAuthorizationCodeBody(String code) {
        return "grant_type=authorization_code" +
                "&client_id=" + URLEncoder.encode(hubspotProperties.getClientId(), StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(hubspotProperties.getClientSecret(), StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(hubspotProperties.getRedirectUri(), StandardCharsets.UTF_8) +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
    }

    private String buildRefreshTokenBody(String refreshToken) {
        return "grant_type=refresh_token" +
                "&client_id=" + URLEncoder.encode(hubspotProperties.getClientId(), StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(hubspotProperties.getClientSecret(), StandardCharsets.UTF_8) +
                "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
    }

    private String sendTokenRequest(String body) {
        return webClient.post()
                .uri(hubspotProperties.getTokenUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new HubSpotApiException("Erro ao comunicar com HubSpot: " + errorBody)))
                )
                .bodyToMono(String.class)
                .block();
    }

    private TokenResponseDto parseAndStoreTokens(String response, HttpSession session) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);

            String accessToken = json.get("access_token").asText();
            String refreshToken = json.has("refresh_token")
                    ? json.get("refresh_token").asText()
                    : refreshTokenStore.get(session.getId());

            accessTokenCache.put(session.getId(), accessToken);
            refreshTokenStore.put(session.getId(), refreshToken);

            int expiresIn = json.get("expires_in").asInt();
            accessTokenExpirationCache.put(session.getId(), Instant.now().plusSeconds(expiresIn));
            session.setAttribute("accessToken", accessToken);
            session.setAttribute("refreshToken", refreshToken);

            return new TokenResponseDto(accessToken, refreshToken, expiresIn);
        } catch (Exception e) {
            throw new HubSpotApiException("Erro ao processar resposta do HubSpot");
        }
    }

    public String getValidAccessToken(HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        String refreshToken = (String) session.getAttribute("refreshToken");
        Instant expiration = accessTokenExpirationCache.get(session.getId());

        if (accessToken == null || refreshToken == null) {
            throw new HubSpotAuthorizationException("Tokens não encontrados na sessão.");
        }

        if (expiration != null && expiration.isBefore(Instant.now())) {
            try {
                TokenResponseDto refreshedTokens = refreshAccessToken(session);
                return refreshedTokens.accessToken();
            } catch (Exception ex) {
                throw new HubSpotAuthorizationException("Falha ao renovar o Access Token: " + ex.getMessage());
            }
        }
        return accessToken;
    }}