package br.com.meetime.hubspotintegrator.dto.response;

public record TokenResponseDto(
        String accessToken,
        String refreshToken,
        int expiresIn
) {
}