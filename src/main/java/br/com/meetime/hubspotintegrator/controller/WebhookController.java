package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.config.HubspotProperties;
import br.com.meetime.hubspotintegrator.config.HubspotSignatureValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final HubspotProperties hubspotProperties;

    public WebhookController(HubspotProperties hubspotProperties) {
        this.hubspotProperties = hubspotProperties;
    }

    @PostMapping
    public ResponseEntity<String> receiveWebhook(@RequestHeader("X-HubSpot-Signature") String signature,
                                                 @RequestBody String requestBody) {
        if (!HubspotSignatureValidator.isValid(signature, requestBody, hubspotProperties.getClientSecret())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Assinatura inválida.");
        }

        return ResponseEntity.ok(requestBody + "Webhook processado com sucesso.");
    }
}
