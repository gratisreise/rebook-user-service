package com.example.rebookuserservice.model.feigns;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorsRequest {
    private List<String> userIds;
}
