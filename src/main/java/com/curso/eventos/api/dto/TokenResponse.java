package com.curso.eventos.api.dto;

import java.time.Instant;

public record TokenResponse(
    String accessToken,
    String tokenType,
    long expiresIn,
    Instant expiresAt
) {
}
