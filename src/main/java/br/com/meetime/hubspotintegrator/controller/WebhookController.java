package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.config.HubspotProperties;
import br.com.meetime.hubspotintegrator.util.HubspotSignatureValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/webhook")
public class WebhookController {

    private final HubspotProperties hubspotProperties;

    @PostMapping
    public ResponseEntity<String> receiveWebhook(@RequestHeader("X-HubSpot-Signature") String signature,
                                                 @RequestBody String requestBody) {
        log.info("Recebido webhook: {}", requestBody);
        if (!HubspotSignatureValidator.isValid(signature, requestBody, hubspotProperties.getClientSecret())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Assinatura inválida.");
        }

        return ResponseEntity.ok(requestBody + "Webhook processado com sucesso.");
    }
}
