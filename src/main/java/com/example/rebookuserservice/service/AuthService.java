package com.example.rebookuserservice.service;

import com.example.rebookuserservice.exception.CMissingDataException;
import com.example.rebookuserservice.feigns.KeycloakClient;
import com.example.rebookuserservice.model.KeycloakRequest;
import com.example.rebookuserservice.model.LoginRequest;
import com.example.rebookuserservice.model.RefreshResponse;
import com.example.rebookuserservice.model.TokenResponse;
import com.example.rebookuserservice.model.UserInfo;
import com.example.rebookuserservice.model.entity.Users;
import com.example.rebookuserservice.repository.UserRepository;
import com.example.rebookuserservice.utils.JwtUtil;
import com.example.rebookuserservice.utils.KeycloakJwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final KeycloakClient keycloakClient;
    private final KeycloakJwtUtil keycloakJwtUtil;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    private String basicName = "닉네임";
    private String refreshPrefix = "refresh:";

    @Value("${aws.basic}")
    private String baseImageUrl;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        String code = request.getCode();
        log.info("Login request code: {}", code);

        String keycloakToken = getKeycloakToken(code);
        log.info("Login request keycloak token: {}", keycloakToken);

        UserInfo userInfo = keycloakJwtUtil.getUserInfo(keycloakToken);
        log.info("Login request userInfo: {}", userInfo.toString());
        String userId = userInfo.getUserId();

        if(!userRepository.existsById(userId)){
            Users users = new Users(userInfo);
            users.setNickname(basicName + userId);
            users.setProfileImage(baseImageUrl);
            userRepository.save(users);
            log.info("Save UserInfo: {}", userInfo);
        }

        String accessToken = jwtUtil.createAccessToken(userId);
        String refreshToken = jwtUtil.createRefreshToken(userId);

        cacheRefreshToken(refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    public RefreshResponse refresh(String refreshToken) {
        String cachedRefreshToken = refreshPrefix + refreshToken;
        log.info("Refresh request refresh token: {}", cachedRefreshToken);
        if(redisService.get(cachedRefreshToken).isEmpty()){
            throw new CMissingDataException("Invalid refresh token");
        }

        String userId = jwtUtil.getUserId(refreshToken);
        String accessToken = jwtUtil.createAccessToken(userId);

        return new RefreshResponse(accessToken);
    }

    private String getKeycloakToken(String code) {
        KeycloakRequest keycloakRequest = new KeycloakRequest(code);
        log.info("Get keycloak token request: {}", keycloakRequest);
        return keycloakClient.getKeycloakToken(keycloakRequest)
            .getAccessToken();
    }

    private void cacheRefreshToken(String refreshToken) {
        String refreshPrefix = "refresh:";

        redisService.set(refreshPrefix + refreshToken, "true");
        log.info("Cache refresh token: {}", refreshPrefix + refreshToken);
    }
}
