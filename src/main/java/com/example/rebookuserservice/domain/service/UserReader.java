package com.example.rebookuserservice.domain.service;

import com.example.rebookuserservice.common.exception.UserException;
import com.example.rebookuserservice.domain.model.entity.Users;
import com.example.rebookuserservice.domain.model.dto.request.AuthorsRequest;
import com.example.rebookuserservice.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReader {
    private final UserRepository userRepository;

    //단일 유저 정보 조회
    public Users getUser(String userId) {
        // 존재하지 않는 유저
        return userRepository.findById(userId)
            .orElseThrow(UserException::userNotFound);
    }

    public List<String> getAuthors(AuthorsRequest request) {
        return request.userIds().stream()
            .map(id -> userRepository.findById(id)
                // 존재하지 않는 유저
                .orElseThrow(UserException::userNotFound)
                .getNickname())
            .toList();
    }

}
