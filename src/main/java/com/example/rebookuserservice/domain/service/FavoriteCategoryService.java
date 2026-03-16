package com.example.rebookuserservice.domain.service;

import com.example.rebookuserservice.domain.model.dto.request.CategoryRequest;
import com.example.rebookuserservice.domain.service.reader.FavoriteCategoryReader;
import com.example.rebookuserservice.domain.service.writer.FavoriteCategoryWriter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteCategoryService {

  private final FavoriteCategoryWriter favoriteCategoryWriter;
  private final FavoriteCategoryReader favoriteCategoryReader;

  @Transactional
  public void postCategories(String userId, CategoryRequest request) {
    favoriteCategoryWriter.postCategories(userId, request);
  }

  @Transactional(readOnly = true)
  public List<String> findUserIdsByCategory(String category) {
    return favoriteCategoryReader.findUserIdsByCategory(category);
  }
}
