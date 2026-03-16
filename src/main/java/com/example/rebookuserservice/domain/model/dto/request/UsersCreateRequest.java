package com.example.rebookuserservice.domain.model.dto.request;

import com.example.rebookuserservice.domain.model.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UsersCreateRequest(
    @NotBlank @Email String email, @NotBlank @Length(min = 3, max = 100) String nickname) {

  public Users toEntity(String image, String userId) {
    return Users.builder().id(userId).email(email).nickname(nickname).profileImage(image).build();
  }
}
