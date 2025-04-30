package br.com.meetime.hubspotintegrator.controller;

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
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/authorization")
    public void getAuthorizationUrl(HttpServletResponse response) throws IOException {
        String authorizationUrl = oAuthService.generateAuthorizationUrl();
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/callback")
    public void handleCallback(@RequestParam("code") String code,
                               HttpServletResponse response) throws IOException {
        log.info("Código recebido no callback: {}", code);
        oAuthService.exchangeCodeForToken(code);
        response.sendRedirect("/create-contact.html");
    }
}