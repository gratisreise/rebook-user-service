package com.example.rebookuserservice.controller;

import com.example.rebookuserservice.model.OAuthUsersRequest;
import com.example.rebookuserservice.model.UsersCreateRequest;
import com.example.rebookuserservice.model.feigns.AuthorsRequest;
import com.example.rebookuserservice.service.FavoriteCategoryReader;
import com.example.rebookuserservice.service.UserReader;
import com.example.rebookuserservice.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="유저조회api", description = "타서비스의 조회를 위한 api")
@Slf4j
public class ClientController {
    private final UserReader userReader;
    private final FavoriteCategoryReader favoriteCategoryReader;
    private final UsersService usersService;

    @PostMapping("/authors")
    @Operation(summary = "유저이름조회")
    public List<String> getAuthors(@RequestBody AuthorsRequest request) {
        return userReader.getAuthors(request);
    }

    @GetMapping("/alarms/books")
    @Operation(summary = "유저id목록조회")
    public  List<String> getUserIdsByCategory(@RequestParam String category) {
        return favoriteCategoryReader.findUserIdsByCategory(category);
    }

    @PostMapping("/sign-up")
    @Operation(summary = "회원생성")
    public String createUser(@RequestBody UsersCreateRequest request){
        log.info("회원가입 들어옴");
        log.info("request:{}", request);
        return usersService.createUser(request);
    }

    @PostMapping("/oauth/login")
    @Operation(summary = "소셜로그인")
    public String createUser(@RequestBody OAuthUsersRequest request){
        return usersService.createUser(request);
    }

}
