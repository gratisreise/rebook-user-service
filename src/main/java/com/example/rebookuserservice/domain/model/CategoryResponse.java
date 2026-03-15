package com.example.rebookuserservice.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {
    private List<String> categories;
    public CategoryResponse(List<String> categories) {
        this.categories = categories;
    }
}
