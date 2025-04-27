package br.com.meetime.hubspotintegrator.dto.request;

public record ContactPropertiesDTO(
        String email,
        String firstname,
        String lastname,
        String phone,
        String company,
        String website,
        String lifecyclestage
) {
}
