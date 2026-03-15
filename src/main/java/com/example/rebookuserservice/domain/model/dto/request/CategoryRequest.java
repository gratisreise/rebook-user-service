package com.example.rebookuserservice.domain.model.dto.request;

import java.util.List;

public record CategoryRequest(
    List<String> categories
) {
}
