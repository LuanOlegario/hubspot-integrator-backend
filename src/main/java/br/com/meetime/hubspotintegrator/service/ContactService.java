package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.dto.request.CreateContactDto;
import br.com.meetime.hubspotintegrator.dto.response.ContactResponseDto;
import br.com.meetime.hubspotintegrator.dto.response.TokenResponseDto;
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

import static br.com.meetime.hubspotintegrator.constansts.HubspotConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final TokenService tokenService;
    private final RestClient restClient;

    @Value("${hubspot.url.contacts}")
    private String urlContacts;

    @RateLimiter(name = HUBSPOT_API_RATELIMITER)
    public ContactResponseDto createContact(CreateContactDto createContactDto) {
        try {
            TokenResponseDto token = tokenService.getValidToken();

            RestClient clientWithAuth = restClient.mutate()
                    .defaultHeader(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token.accessToken())
                    .build();

            return clientWithAuth.post()
                    .uri(urlContacts)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createContactDto)
                    .retrieve()
                    .body(ContactResponseDto.class);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                throw new HubSpotRateLimitExceededException(RATE_LIMIT_EXCEEDED);
            }

            if (e.getStatusCode().equals(HttpStatus.CONFLICT)) {
                throw new HubSpotBadRequestException(CONFLICT_ON_CREATE);
            }

            throw new HubSpotApiException(HUBSPOT_API_ERROR_PREFIX + e.getMessage());
        }
    }
}








