package com.example.rebookuserservice.domain.model.dto.response;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {
}
