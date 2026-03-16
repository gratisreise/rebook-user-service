package com.example.rebookuserservice.domain.service.reader;

import com.example.rebookuserservice.domain.repository.FavoriteCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteCategoryReader {
  private final FavoriteCategoryRepository favoriteCategoryRepository;

  public List<String> findUserIdsByCategory(String category) {
    return favoriteCategoryRepository.findUserIdsByCategory(category);
  }
}
