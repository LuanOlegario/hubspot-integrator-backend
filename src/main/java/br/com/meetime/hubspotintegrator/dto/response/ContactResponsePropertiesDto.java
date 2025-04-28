package br.com.meetime.hubspotintegrator.dto.response;

public record ContactResponsePropertiesDto(
        String company,
        String createdate,
        String email,
        String firstname,
        String lastname,
        String lifecyclestage,
        String phone,
        String website
) {
}
