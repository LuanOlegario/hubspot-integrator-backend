package br.com.meetime.hubspotintegrator.dto.response;

import lombok.Builder;

public record TokenResponseDto(
       String accessToken,
       String refreshToken,
       int expiresIn
) {
}
