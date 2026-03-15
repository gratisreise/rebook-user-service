package com.example.rebookuserservice.domain.model;

import com.example.rebookuserservice.domain.model.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
public class UsersCreateRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 3, max = 100)
    private String nickname;

    public Users toEntity(String image, String userId){
        return Users.builder()
            .id(userId)
            .email(email)
            .nickname(nickname)
            .profileImage(image)
            .build();
    }


}
