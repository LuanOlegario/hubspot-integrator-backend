package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static br.com.meetime.hubspotintegrator.constants.Constants.ACCESS_TOKEN;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/oauth")
public class OAuthController {

    private final OAuthService OAuthService;

    @GetMapping("/authorize")
    public void getAuthorizationUrl(HttpServletResponse response) throws IOException {
        String authorizationUrl = OAuthService.generateAuthorizationUrl();
        log.info("Redirecionando para autorização do HubSpot: {}", authorizationUrl);
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/callback")
    public void handleCallback(@RequestParam("code") String code,
                               HttpSession session, HttpServletResponse response) throws IOException {
        OAuthService.exchangeCodeForToken(code, session);
        String accessToken = (String) session.getAttribute(ACCESS_TOKEN);
        log.info("Usuário autenticado com sucesso no HubSpot. Access Token: {}", accessToken);
        response.sendRedirect("/create-contact.html");
    }
}