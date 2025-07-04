package com.example.rebookuserservice.model.entity.compositekey;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class UserBookId {
    private String userId;
    private Long bookId;
}
