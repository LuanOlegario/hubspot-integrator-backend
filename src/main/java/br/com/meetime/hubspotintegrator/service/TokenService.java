package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.config.HubspotProperties;
import br.com.meetime.hubspotintegrator.dto.response.TokenResponseDto;
import br.com.meetime.hubspotintegrator.exception.HubSpotApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import static br.com.meetime.hubspotintegrator.constansts.HubspotConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j

public class TokenService {

    private final ConcurrentHashMap<String, TokenResponseDto> tokenMap = new ConcurrentHashMap<>();
    private final HubspotProperties hubspotProperties;
    private final RestClient restClient;

    public void storeToken(String key, TokenResponseDto token) {
        if (token == null || token.accessToken() == null) {
            throw new IllegalArgumentException("Token inválido ao tentar armazenar.");
        }
        tokenMap.put(key, token);
    }

    public TokenResponseDto getToken(String key) {
        return tokenMap.get(key);
    }

    public TokenResponseDto getValidToken() {
        TokenResponseDto token = tokenMap.get(DEFAULT_TOKEN_KEY);

        if (token == null) {
            throw new HubSpotApiException(TOKEN_NOT_FOUND);
        }

        if (isExpired(token)) {
            log.info("Token expirado. Iniciando renovação...");
            return renewToken();

        }
        return token;
    }

    public TokenResponseDto parseAndStoreTokens(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);

            String accessToken = json.get(ACCESS_TOKEN_KEY).asText();
            String refreshToken = json.has(REFRESH_TOKEN_KEY) ? json.get(REFRESH_TOKEN_KEY).asText() : null;
            int expiresIn = json.get(EXPIRES_IN_KEY).asInt();

            Instant expirationTime = Instant.now().plusSeconds(expiresIn);
            TokenResponseDto tokenResponseDto = new TokenResponseDto(accessToken, refreshToken, expirationTime);

            storeToken(DEFAULT_TOKEN_KEY, tokenResponseDto);

            return tokenResponseDto;
        } catch (Exception e) {
            log.error("Erro ao converter token JSON: {}", e.getMessage(), e);
            throw new HubSpotApiException(ERROR_PROCESSING_TOKENS);
        }
    }

    public TokenResponseDto renewToken() {
        TokenResponseDto currentToken = tokenMap.get(DEFAULT_TOKEN_KEY);

        if (currentToken == null || currentToken.refreshToken() == null) {
            log.error("Token atual ou refresh token ausente.");
            throw new HubSpotApiException(TOKEN_OR_REFRESH_TOKEN_NOT_FOUND);
        }

        String response = requestNewAccessToken(currentToken.refreshToken());
        return parseAndStoreTokens(response);
    }

    private boolean isExpired(TokenResponseDto token) {
        return Instant.now().isAfter(token.expiresIn());
    }

    private String requestNewAccessToken(String refreshToken) {
        try {
            String body = buildRefreshTokenBody(refreshToken);
            return restClient.post()
                    .uri(hubspotProperties.getTokenUrl())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception ex) {
            log.error("Erro ao renovar token com refresh_token: {}", ex.getMessage(), ex);
            throw new HubSpotApiException(COMMUNICATION_ERROR_WITH_HUBSPOT);
        }
    }

    private String buildRefreshTokenBody(String refreshToken) {
        return "grant_type=refresh_token" +
                "&client_id=" + urlEncode(hubspotProperties.getClientId()) +
                "&client_secret=" + urlEncode(hubspotProperties.getClientSecret()) +
                "&refresh_token=" + urlEncode(refreshToken);
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
