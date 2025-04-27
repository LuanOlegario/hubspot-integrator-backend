package br.com.meetime.hubspotintegrator.dto.response;

public record TokenResponseDTO(
       String accessToken,
       String refreshToken,
       int expiresIn
) {
}
