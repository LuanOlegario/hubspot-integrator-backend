package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.config.HubspotProperties;
import br.com.meetime.hubspotintegrator.util.HubspotSignatureValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private HubspotProperties hubspotProperties;

    @Test
    void testReceiveWebhook_validSignature() throws Exception {
        String requestBody = mapper.writeValueAsString(List.of(Map.of(
                "subscriptionType", "contact.creation",
                "objectId", "123456",
                "propertyName", "email",
                "propertyValue", "test@luan.com.br"
        )));

        try (MockedStatic<HubspotSignatureValidator> mockedValidator = mockStatic(HubspotSignatureValidator.class)) {
            mockedValidator.when(() ->
                    HubspotSignatureValidator.isValid("valid-signature", requestBody, hubspotProperties.getClientSecret())
            ).thenReturn(true);

            mockMvc.perform(post("/api/webhook")
                            .header("X-HubSpot-Signature", "valid-signature")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Eventos de criação de contato processados com sucesso."));
        }
    }

    @Test
    void testReceiveWebhook_invalidSignature() throws Exception {
        String requestBody = "[]";

        try (MockedStatic<HubspotSignatureValidator> mockedValidator = mockStatic(HubspotSignatureValidator.class)) {
            mockedValidator.when(() ->
                    HubspotSignatureValidator.isValid("invalid-signature", requestBody, hubspotProperties.getClientSecret())
            ).thenReturn(false);

            mockMvc.perform(post("/api/webhook")
                            .header("X-HubSpot-Signature", "invalid-signature")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Assinatura inválida."));
        }
    }
}
