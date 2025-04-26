package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.service.HubspotOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthController {

    private final HubspotOAuthService hubspotOAuthService;

    @GetMapping("/authorize")
    public void getAuthorizationUrl(HttpServletResponse response) throws IOException {
        response.sendRedirect(hubspotOAuthService.generateAuthorizationUrl());
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam("code") String code,
                                                 HttpSession session) {
        String token = hubspotOAuthService.exchangeCodeForToken(code, session.getId());
        return ResponseEntity.ok(token);
    }
}