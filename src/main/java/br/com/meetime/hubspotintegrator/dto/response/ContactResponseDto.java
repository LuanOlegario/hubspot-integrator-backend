package br.com.meetime.hubspotintegrator.dto.response;

public record ContactResponseDto(
        String id,
        ContactResponsePropertiesDto properties,
        String createdAt
) {
}
