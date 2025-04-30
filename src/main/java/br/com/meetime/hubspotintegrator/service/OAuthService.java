package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.config.HubspotProperties;
import br.com.meetime.hubspotintegrator.dto.response.TokenResponseDto;
import br.com.meetime.hubspotintegrator.exception.HubSpotApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthService {

    private final HubspotProperties hubspotProperties;
    private final TokenStoreService tokenStore;
    @Qualifier("authRestClient")
    private final RestClient authRestClient;


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

    public TokenResponseDto exchangeCodeForToken(String code) {
        String body = buildAuthorizationCodeBody(code);
        String response = sendTokenRequest(body);
        return parseAndStoreTokens(response);
    }

    private String buildAuthorizationCodeBody(String code) {
        return "grant_type=authorization_code" +
                "&client_id=" + URLEncoder.encode(hubspotProperties.getClientId(), StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(hubspotProperties.getClientSecret(), StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(hubspotProperties.getRedirectUri(), StandardCharsets.UTF_8) +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
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
            throw new HubSpotApiException("Erro de comunicação com o HubSpot: " + ex.getMessage());
        }
    }

    private TokenResponseDto parseAndStoreTokens(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);

            String accessToken = json.get("access_token").asText();
            String refreshToken = json.has("refresh_token") ? json.get("refresh_token").asText() : null;
            int expiresIn = json.get("expires_in").asInt();
            Instant expiration = Instant.now().plusSeconds(expiresIn);

            TokenResponseDto tokenResponseDto = new TokenResponseDto(accessToken, refreshToken, expiration);
            tokenStore.store("token", tokenResponseDto);
            return tokenResponseDto;
        } catch (Exception e) {
            throw new HubSpotApiException("Erro ao processar a resposta de tokens.");
        }
    }

    public TokenResponseDto getToken() {
        TokenResponseDto token = tokenStore.get("token");
        if (token == null) {
            throw new HubSpotApiException("Token de acesso ainda não foi gerado ou expirou.");
        }
        return token;
    }
}