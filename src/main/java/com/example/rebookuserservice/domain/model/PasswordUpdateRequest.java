package com.example.rebookuserservice.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateRequest {
    @NotBlank
    private String password;
}
