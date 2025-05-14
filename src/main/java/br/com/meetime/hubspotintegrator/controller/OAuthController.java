package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.documentation.OAuthApiDoc;
import br.com.meetime.hubspotintegrator.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/oauth")
public class OAuthController implements OAuthApiDoc {

    private final OAuthService oAuthService;

    @GetMapping("/authorization")
    public void getAuthorizationUrl(HttpServletResponse response) throws IOException {
        String authorizationUrl = oAuthService.generateAuthorizationUrl();
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/callback")
    public void handleCallback(@RequestParam("code") String code,
                               @RequestParam("state") String state,
                               HttpServletResponse response) throws IOException {
        log.info("Código recebido no callback: {}", code);

        if (!oAuthService.isValidState(state)) {
            log.warn("Tentativa de callback com state inválido: {}", state);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro state inválido. Tente novamente.");
            return;
        }
        oAuthService.exchangeCodeForToken(code);
        response.sendRedirect("/create-contact.html");
    }
}