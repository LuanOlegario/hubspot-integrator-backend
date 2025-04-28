package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.dto.request.CreateContactDto;
import br.com.meetime.hubspotintegrator.dto.response.ContactResponseDto;
import br.com.meetime.hubspotintegrator.exception.HubSpotApiException;
import br.com.meetime.hubspotintegrator.exception.HubSpotRateLimitExceededException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @Value( "${hubspot.url.contacts}")
    private String urlContacts;

    @RateLimiter(name = "hubspotApi")
    public ContactResponseDto createContact(CreateContactDto createContactDto, HttpSession session) {
        log.info("ID da sessão no ContactService (createContact): {}", session.getId());
        try {
            String accessToken = OAuthService.getValidAccessToken(session);
            ContactResponseDto response = webClient.post()
                    .uri(urlContacts)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createContactDto)
                    .retrieve()
                    .bodyToMono(ContactResponseDto.class)
                    .block();

            log.info("Contato criado com sucesso no HubSpot: {}", response);
            return response;
        } catch (WebClientResponseException e) {
            log.error("Erro ao criar contato no HubSpot: Status {} - Body {}", e.getStatusCode(), e.getResponseBodyAsString(), e);

            if (e.getStatusCode() == (HttpStatus.TOO_MANY_REQUESTS)) {
                throw new HubSpotRateLimitExceededException("Limite de requisições da API do HubSpot excedido.");
            }
            throw new HubSpotApiException("Erro ao criar contato no HubSpot.");
        }
    }
}
