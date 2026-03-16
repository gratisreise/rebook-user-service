package com.example.rebookuserservice.domain.model.dto.request;

import com.example.rebookuserservice.domain.model.entity.Users;

public record OAuthUsersRequest(String nickname, String profileImage) {
  public Users toEntity(String userId) {
    return Users.builder().id(userId).nickname(nickname).profileImage(profileImage).build();
  }
}
