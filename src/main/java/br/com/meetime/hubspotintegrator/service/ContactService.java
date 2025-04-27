package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.dto.request.CreateContactDto;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final OAuthService OAuthService;
    private final WebClient webClient;

    @RateLimiter(name = "hubspotApi")
    public void createContact(CreateContactDto createContactDto, HttpSession session) {
        try {
            String accessToken = OAuthService.getValidAccessToken(session);

            String response = webClient.post()
                    .uri("/crm/v3/objects/contacts")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createContactDto)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Contato criado com sucesso no HubSpot: {}", response);
        } catch (WebClientResponseException e) {
            log.error("Erro ao criar contato no HubSpot: Status {} - Body {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Erro ao criar contato no HubSpot.", e);
        }
    }
}
