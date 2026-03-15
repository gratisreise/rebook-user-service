package com.example.rebookuserservice.domain.model.dto.request;

public record UserInfo(
    String userId,
    String username,
    String email,
    String role
) {
}
