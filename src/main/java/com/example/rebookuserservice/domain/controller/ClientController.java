package com.example.rebookuserservice.domain.controller;

import com.example.rebookuserservice.domain.model.dto.request.OAuthUsersRequest;
import com.example.rebookuserservice.domain.model.dto.request.UsersCreateRequest;
import com.example.rebookuserservice.domain.model.dto.request.AuthorsRequest;
import com.example.rebookuserservice.domain.service.FavoriteCategoryReader;
import com.example.rebookuserservice.domain.service.UserReader;
import com.example.rebookuserservice.domain.service.UsersService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class ClientController {
    private final UserReader userReader;
    private final FavoriteCategoryReader favoriteCategoryReader;
    private final UsersService usersService;

    @PostMapping("/authors")
    public List<String> getAuthors(@RequestBody AuthorsRequest request) {
        return userReader.getAuthors(request);
    }

    @GetMapping("/alarms/books")
    public  List<String> getUserIdsByCategory(@RequestParam String category) {
        return favoriteCategoryReader.findUserIdsByCategory(category);
    }

    @PostMapping("/sign-up")
    public String createUser(@RequestBody UsersCreateRequest request){
        log.info("회원가입 들어옴");
        log.info("request:{}", request);
        return usersService.createUser(request);
    }

    @PostMapping("/oauth/login")
    public String createUser(@RequestBody OAuthUsersRequest request){
        return usersService.createUser(request);
    }

}
