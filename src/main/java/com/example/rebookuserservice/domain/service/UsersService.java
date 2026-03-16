package com.example.rebookuserservice.domain.service;

import com.example.rebookuserservice.domain.model.dto.request.AuthorsRequest;
import com.example.rebookuserservice.domain.model.dto.request.OAuthUsersRequest;
import com.example.rebookuserservice.domain.model.dto.request.UsersCreateRequest;
import com.example.rebookuserservice.domain.model.dto.request.UsersUpdateRequest;
import com.example.rebookuserservice.domain.model.dto.response.CategoryResponse;
import com.example.rebookuserservice.domain.model.dto.response.UsersResponse;
import com.example.rebookuserservice.domain.model.entity.Users;
import com.example.rebookuserservice.domain.repository.FavoriteCategoryRepository;
import com.example.rebookuserservice.domain.service.reader.FavoriteCategoryReader;
import com.example.rebookuserservice.domain.service.reader.UserReader;
import com.example.rebookuserservice.domain.service.writer.FavoriteCategoryWriter;
import com.example.rebookuserservice.domain.service.writer.UsersWriter;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {
    private final UserReader userReader;
    private final UsersWriter usersWriter;
    private final FavoriteCategoryReader favoriteCategoryReader;
    private final FavoriteCategoryWriter favoriteCategoryWriter;
    private final FavoriteCategoryRepository favoriteCategoryRepository;

    //유저 정보 조회
    @Transactional(readOnly = true)
    public UsersResponse getUser(String userId) {
        Users user = userReader.getUser(userId);
        return new UsersResponse(user);
    }

    @Transactional
    public void updateUser(String userId, UsersUpdateRequest request, MultipartFile file) throws IOException {
        usersWriter.updateUser(userId, request, file);
    }

    @Transactional
    public void deleteUser(String userId) {
        usersWriter.deleteUser(userId);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategories(String userId) {
        List<String> categories = getFavoriteCategories(userId);
        return new CategoryResponse(categories);
    }

    @Transactional(readOnly = true)
    public List<String> getRecommendedCategories(String userId) {
        return getFavoriteCategories(userId);
    }

    private List<String> getFavoriteCategories(String userId) {
        return favoriteCategoryRepository
            .findByFavoriteCategoryIdUserId(userId)
            .stream()
            .map(f -> f.getFavoriteCategoryId().getCategory())
            .toList();
    }

    @Transactional(readOnly = true)
    public UsersResponse getUserOther(String userId) {
        Users user = userReader.getUser(userId);
        return new UsersResponse(user);
    }

    @Transactional
    public String createUser(UsersCreateRequest request) {
        return usersWriter.createUser(request);
    }

    @Transactional
    public String createUser(OAuthUsersRequest request) {
        return usersWriter.createUser(request);
    }

    @Transactional(readOnly = true)
    public List<String> getAuthors(AuthorsRequest request) {
        return userReader.getAuthors(request);
    }
}
