package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.config.HubspotProperties;
import br.com.meetime.hubspotintegrator.documentation.WebhookApiDoc;
import br.com.meetime.hubspotintegrator.dto.response.HubspotWebhookEventDto;
import br.com.meetime.hubspotintegrator.util.HubspotSignatureValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/webhook")
public class WebhookController implements WebhookApiDoc {

    private final HubspotProperties hubspotProperties;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<String> receiveWebhook(@RequestHeader("X-HubSpot-Signature") String signature,
                                                 @RequestBody String requestBody) {
        log.info("Recebido webhook: {}", requestBody);

        boolean isValid = HubspotSignatureValidator.isValid(signature, requestBody, hubspotProperties.getClientSecret(), "v1");

        if (!isValid) {
            log.warn("Assinatura inválida recebida no webhook.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Assinatura inválida.");
        }

        try {
            List<HubspotWebhookEventDto> events = objectMapper.readValue(
                    requestBody,
                    new TypeReference<List<HubspotWebhookEventDto>>() {
                    }
            );

            events.stream()
                    .filter(event -> "CONTACT_CREATION_OK".equalsIgnoreCase(event.subscriptionType()))
                    .forEach(event -> {
                        log.info("Contato criado. ID: {}, Email: {}", event.objectId());
                    });

            return ResponseEntity.ok("Eventos de criação de contato processados com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao processar webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar o webhook.");
        }
    }
}
