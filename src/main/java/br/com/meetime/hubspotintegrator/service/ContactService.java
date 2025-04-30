package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.dto.request.CreateContactDto;
import br.com.meetime.hubspotintegrator.dto.response.ContactResponseDto;
import br.com.meetime.hubspotintegrator.exception.HubSpotApiException;
import br.com.meetime.hubspotintegrator.exception.HubSpotBadRequestException;
import br.com.meetime.hubspotintegrator.exception.HubSpotRateLimitExceededException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;


@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final OAuthService oAuthService;
    private final RestClient restClient;

    @Value("${hubspot.url.contacts}")
    private String urlContacts;

    @RateLimiter(name = "hubspot-api-rate-limiter")
    public ContactResponseDto createContact(CreateContactDto createContactDto, String acessToken) {
        try {
            ContactResponseDto response = restClient.post()
                    .uri(urlContacts)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + acessToken) //todo e colocar interceptador
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createContactDto)
                    .retrieve()
                    .body(ContactResponseDto.class);

            return response;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                throw new HubSpotRateLimitExceededException("Limite de requisições excedido.");
            }

            if (e.getStatusCode().equals(HttpStatus.CONFLICT)) {
                throw new HubSpotBadRequestException("Erro de conflito ao criar contato.");
            }

            throw new HubSpotApiException("Erro na API do HubSpot: " + e.getMessage());
        }
    }
}








