package br.com.meetime.hubspotintegrator.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TokenResponseDto(
        String accessToken,
        String refreshToken,
        Instant expiresIn
) {
}