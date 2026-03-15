package com.example.rebookuserservice.model;

import com.example.rebookuserservice.model.entity.Users;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthUsersRequest {

    private String nickname;
    private String profileImage;


    public Users toEntity(String userId) {
        return Users.builder()
            .id(userId)
            .nickname(nickname)
            .profileImage(profileImage)
            .build();
    }

}
