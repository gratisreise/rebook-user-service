package com.example.rebookuserservice.domain.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UsersUpdateRequest(
    @NotBlank
    String email,

    @NotBlank
    @Length(min = 3, max = 20)
    String nickname
) {

}
