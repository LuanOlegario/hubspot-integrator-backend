package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.config.HubspotProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class OAuthServiceTest {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private HubspotProperties properties;

    @Test
    void testGenerateAuthorizationUrl() {
        String url = oAuthService.generateAuthorizationUrl();

        assertTrue(url.contains("client_id=" + properties.getClientId()));
        assertTrue(url.contains("redirect_uri=" + properties.getRedirectUri()));
        assertTrue(url.contains("scope="));
        assertTrue(url.contains("response_type=code"));
    }
}
