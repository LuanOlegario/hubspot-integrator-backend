package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.RedirectView;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/oauth")
public class OAuthController {

    private final OAuthService OAuthService;

    @GetMapping("/authorize")
    public RedirectView getAuthorizationUrl() {
        String authorizationUrl = OAuthService.generateAuthorizationUrl();
        log.info("Redirecionando para autorização do HubSpot: {}", authorizationUrl);
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public void handleCallback(@RequestParam("code") String code,
                               HttpSession session, HttpServletResponse response) throws IOException {
        OAuthService.exchangeCodeForToken(code, session);
        log.info("Usuário autenticado com sucesso no HubSpot.");
        response.sendRedirect("/create-contact.html");
    }
}