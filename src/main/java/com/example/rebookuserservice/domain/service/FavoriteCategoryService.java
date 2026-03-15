package com.example.rebookuserservice.service;

import com.example.rebookuserservice.model.CategoryRequest;
import com.example.rebookuserservice.model.entity.FavoriteCategory;
import com.example.rebookuserservice.model.entity.compositekey.FavoriteCategoryId;
import com.example.rebookuserservice.repository.FavoriteCategoryRepository;
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
        List<String> categories = request.getCategories();
        categories.forEach(category -> {
            FavoriteCategoryId favoriteCategoryId = new FavoriteCategoryId(userId, category);
            FavoriteCategory favoriteCategory = new FavoriteCategory(favoriteCategoryId);
            favoriteCategoryRepository.save(favoriteCategory);
        });
    }
}
