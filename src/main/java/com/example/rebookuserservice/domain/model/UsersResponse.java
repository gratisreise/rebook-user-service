package com.example.rebookuserservice.model;

import com.example.rebookuserservice.model.entity.Users;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersResponse {
    private String email;
    private String nickname;
    private String profileImage;
    private String userId;

    public UsersResponse(Users user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.userId = user.getId();
    }
}
