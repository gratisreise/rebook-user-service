package com.example.rebookuserservice.domain.service;

import com.example.rebookuserservice.domain.model.dto.request.CategoryRequest;
import com.example.rebookuserservice.domain.model.entity.FavoriteCategory;
import com.example.rebookuserservice.domain.model.entity.compositekey.FavoriteCategoryId;
import com.example.rebookuserservice.domain.repository.FavoriteCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteCategoryService {

    private final FavoriteCategoryRepository favoriteCategoryRepository;

    @Transactional
    public void postCategories(String userId, CategoryRequest request) {
        List<String> categories = request.categories();
        categories.forEach(category -> {
            FavoriteCategoryId favoriteCategoryId = new FavoriteCategoryId(userId, category);
            FavoriteCategory favoriteCategory = new FavoriteCategory(favoriteCategoryId);
            favoriteCategoryRepository.save(favoriteCategory);
        });
    }
}
