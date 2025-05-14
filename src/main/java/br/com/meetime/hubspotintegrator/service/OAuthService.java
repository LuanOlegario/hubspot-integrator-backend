package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.config.HubspotProperties;
import br.com.meetime.hubspotintegrator.dto.response.TokenResponseDto;
import br.com.meetime.hubspotintegrator.exception.HubSpotApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

    private final HubspotProperties hubspotProperties;
    private final RestClient authRestClient;
    private final TokenService tokenService;

    private final Map<String, Instant> stateMap = new ConcurrentHashMap<>();
    private static final long STATE_EXPIRATION_SECONDS = 300;

    public String generateAuthorizationUrl() {
        String encodedScopes = URLEncoder.encode(hubspotProperties.getScope(), StandardCharsets.UTF_8);
        String state = generateState();

        return UriComponentsBuilder
                .fromUriString(hubspotProperties.getAuthorizationUrl())
                .queryParam("client_id", hubspotProperties.getClientId())
                .queryParam("redirect_uri", hubspotProperties.getRedirectUri())
                .queryParam("scope", encodedScopes)
                .queryParam("response_type", "code")
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    public TokenResponseDto exchangeCodeForToken(String code) {
        String body = buildAuthorizationCodeBody(code);
        String response = sendTokenRequest(body);
        return tokenService.parseAndStoreTokens(response);
    }

    private String buildAuthorizationCodeBody(String code) {
        return "grant_type=authorization_code" +
                "&client_id=" + urlEncode(hubspotProperties.getClientId()) +
                "&client_secret=" + urlEncode(hubspotProperties.getClientSecret()) +
                "&redirect_uri=" + urlEncode(hubspotProperties.getRedirectUri()) +
                "&code=" + urlEncode(code);
    }

    private String sendTokenRequest(String body) {
        try {
            return authRestClient.post()
                    .uri(hubspotProperties.getTokenUrl())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception ex) {
            log.error("Erro ao obter token com authorization_code: {}", ex.getMessage());
            throw new HubSpotApiException("Erro de comunicação com o HubSpot.");
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String generateState() {
        String state = UUID.randomUUID().toString();
        stateMap.put(state, Instant.now().plusSeconds(STATE_EXPIRATION_SECONDS));
        return state;
    }

    public boolean isValidState(String state) {
        Instant expiration = stateMap.get(state);

        if (expiration == null || Instant.now().isAfter(expiration)) {
            log.warn("State inválido ou expirado: {}", state);
            return false;
        }

        stateMap.remove(state);
        return true;
    }
}
