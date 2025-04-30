package br.com.meetime.hubspotintegrator.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

public interface OAuthApiDoc {

    @Operation(summary = "Redireciona o usuário para a autorização do HubSpot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirecionamento para a URL de autorização")
    })
    @GetMapping("/authorization")
    void getAuthorizationUrl(HttpServletResponse response) throws IOException;

    @Operation(summary = "Callback da autorização do HubSpot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirecionamento após troca de token"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'code' ausente ou inválido")
    })
    @GetMapping("/callback")
    void handleCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException;
}
