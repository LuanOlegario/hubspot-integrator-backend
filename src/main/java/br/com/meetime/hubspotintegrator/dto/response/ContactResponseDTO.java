package br.com.meetime.hubspotintegrator.dto.response;

public record ContactResponseDTO(
        String id,
        ContactResponsePropertiesDto properties,
        String createdAt,
        String updatedAt,
        boolean archived
) {
}
