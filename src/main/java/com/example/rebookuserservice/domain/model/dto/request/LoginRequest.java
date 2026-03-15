package com.example.rebookuserservice.domain.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank
    String accessToken
) {
}
