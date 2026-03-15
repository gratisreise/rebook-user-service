package com.example.rebookuserservice.service;

import com.example.rebookuserservice.repository.FavoriteCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteCategoryReader {
    private final FavoriteCategoryRepository favoriteCategoryRepository;

    public List<String> findUserIdsByCategory(String category) {
        return favoriteCategoryRepository.findUserIdsByCategory(category);
    }
}
