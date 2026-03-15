package com.example.rebookuserservice.domain.controller;


import com.example.rebookuserservice.domain.model.dto.request.CategoryRequest;
import com.example.rebookuserservice.domain.service.FavoriteCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "선호카테고리api")
public class FavoriteCategoryController {

    private final FavoriteCategoryService favoriteCategoryService;

    @PostMapping("/categories")
    @Operation(summary = "선호카테고리등록")
    public CommonResult postFavoriteCategory(@RequestHeader("X-User-Id")String userId,
        @RequestBody CategoryRequest request) {
        favoriteCategoryService.postCategories(userId, request);
        return ResponseService.getSuccessResult();
    }

}
