package com.example.rebookuserservice.domain.controller;


import com.example.rebookuserservice.domain.model.dto.request.CategoryRequest;
import com.example.rebookuserservice.domain.service.FavoriteCategoryService;
import com.rebook.common.core.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FavoriteCategoryController {

    private final FavoriteCategoryService favoriteCategoryService;

    @PostMapping("/categories")
    public ResponseEntity<SuccessResponse<Void>> postFavoriteCategory(@RequestHeader("X-User-Id")String userId,
        @RequestBody CategoryRequest request) {
        favoriteCategoryService.postCategories(userId, request);
        return SuccessResponse.toNoContent();
    }

}
