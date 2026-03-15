package com.example.rebookuserservice.domain.model.dto.response;

import com.example.rebookuserservice.domain.model.entity.Users;

public record UsersResponse(
    String email,
    String nickname,
    String profileImage,
    String userId
) {
    public UsersResponse(Users user) {
        this(user.getEmail(), user.getNickname(), user.getProfileImage(), user.getId());
    }
}
