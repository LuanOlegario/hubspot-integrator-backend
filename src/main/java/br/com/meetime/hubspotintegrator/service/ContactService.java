package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.dto.request.CreateContactDto;
import br.com.meetime.hubspotintegrator.dto.response.ContactResponseDto;
import br.com.meetime.hubspotintegrator.exception.HubSpotApiException;
import br.com.meetime.hubspotintegrator.exception.HubSpotBadRequestException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static br.com.meetime.hubspotintegrator.constants.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final OAuthService oAuthService;
    private final RestClient restClient;

    @Value("${hubspot.url.contacts}")
    private String urlContacts;

    @RateLimiter(name = HUBSPOT_API_RATE_LIMITER)
    public ContactResponseDto createContact(CreateContactDto createContactDto, HttpSession session) {
        try {
            String accessToken = oAuthService.getValidAccessToken(session);

            ContactResponseDto response = restClient.post()
                    .uri(urlContacts)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createContactDto)
                    .retrieve()
                    .body(ContactResponseDto.class);

            log.info("Contato criado com sucesso no HubSpot: {}", response);
            return response;
        } catch (HttpClientErrorException e) {
            log.error("Erro ao criar contato no HubSpot: Status {} - Body {}", e.getStatusCode(), e.getResponseBodyAsString(), e);

            if (e.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new HubSpotRateLimitExceededException(HUBSPOT_RATE_LIMIT_ERROR_MESSAGE);
            }

            if (e.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
                log.warn("Tentativa de criar contato duplicado no HubSpot. Email: {}", createContactDto.properties().email());
                throw new HubSpotBadRequestException(HUBSPOT_CONTACT_CONFLICT);
            }
        }
        throw new HubSpotApiException(HUBSPOT_CONTACT_CREATE_ERROR);
    }
}
