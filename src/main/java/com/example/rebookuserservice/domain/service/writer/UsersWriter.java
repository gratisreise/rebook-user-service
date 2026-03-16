package com.example.rebookuserservice.domain.service.writer;

import com.example.rebookuserservice.clientfeign.notification.NotificationClient;
import com.example.rebookuserservice.common.exception.UserException;
import com.example.rebookuserservice.domain.model.dto.request.OAuthUsersRequest;
import com.example.rebookuserservice.domain.model.dto.request.UsersCreateRequest;
import com.example.rebookuserservice.domain.model.dto.request.UsersUpdateRequest;
import com.example.rebookuserservice.domain.model.entity.Users;
import com.example.rebookuserservice.domain.repository.UserRepository;
import com.example.rebookuserservice.domain.service.reader.UserReader;
import com.example.rebookuserservice.external.s3.S3Service;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsersWriter {
    private final UserRepository userRepository;
    private final UserReader userReader;
    private final S3Service s3Service;
    private final NotificationClient notificationClient;

    @Value("${aws.basic}")
    private String baseImageUrl;

    public void updateUser(String userId, UsersUpdateRequest request, MultipartFile file) throws IOException {
        // 존재하지 않는 유저
        if(!userRepository.existsById(userId)) {
            throw UserException.userNotFound();
        }

        Users user = userReader.getUser(userId);
        String newEmail = request.email();
        String newNickname= request.nickname();

        if(file != null) {
            String imageUrl = s3Service.upload(file);
            user.setProfileImage(imageUrl);
            log.info("Image url: {}", imageUrl);
        }

        // 중복된 이메일
        if(!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)){
            throw UserException.duplicatedEmail();
        }

        // 중복된 닉네임
        if(!user.getNickname().equals(newNickname) && userRepository.existsByNickname(newNickname)){
            throw UserException.duplicatedNickname();
        }

        Users updatedUser = user.update(request);
        log.info("User updated: {}", updatedUser);
    }

    public void deleteUser(String userId) {
        // 존재하지 않는 유저
        if(!userRepository.existsById(userId)) {
            throw UserException.userNotFound();
        }
        userRepository.deleteById(userId);
    }

    public String createUser(UsersCreateRequest request) {
        String userId = generateUserId();
        Users user = request.toEntity(baseImageUrl, userId);
        Users savedUsers = userRepository.save(user);
        notificationClient.createAllSettings(userId);
        return savedUsers.getId();
    }

    public String createUser(OAuthUsersRequest request) {
        String userId = generateUserId();
        Users user = request.toEntity(userId);
        Users savedUsers = userRepository.save(user);
        notificationClient.createAllSettings(userId);
        return savedUsers.getId();
    }

    private String generateUserId(){
        return UUID.randomUUID().toString().replaceAll(",","");
    }
}
