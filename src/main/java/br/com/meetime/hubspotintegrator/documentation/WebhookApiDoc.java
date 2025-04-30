package br.com.meetime.hubspotintegrator.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static br.com.meetime.hubspotintegrator.constansts.HubspotConstants.SIGNATURE_HEADER;

public interface WebhookApiDoc {

    @Operation(summary = "Recebe eventos do webhook do HubSpot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook processado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao processar o webhook"),
            @ApiResponse(responseCode = "401", description = "Assinatura inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    ResponseEntity<String> receiveWebhook(
            @RequestHeader(SIGNATURE_HEADER) String signature,
            @RequestBody String requestBody);
}
