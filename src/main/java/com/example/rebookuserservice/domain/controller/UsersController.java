package com.example.rebookuserservice.domain.controller;


import com.example.rebookuserservice.domain.model.dto.request.PasswordUpdateRequest;
import com.example.rebookuserservice.domain.model.dto.request.UsersUpdateRequest;
import com.example.rebookuserservice.domain.model.dto.response.CategoryResponse;
import com.example.rebookuserservice.domain.model.dto.response.UsersResponse;
import com.example.rebookuserservice.domain.service.UsersService;
import com.rebook.common.auth.PassportProto.Passport;
import com.rebook.common.auth.PassportUser;
import com.rebook.common.core.response.SuccessResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final UsersService usersService;


    @GetMapping("/test")
    public String test(@PassportUser Passport passport){
        return passport.getUserId();
    }


    @GetMapping
    public ResponseEntity<SuccessResponse<UsersResponse>> getUser(@PassportUser String userId) {
        return SuccessResponse.toOk(usersService.getUser(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SuccessResponse<UsersResponse>> getUserOther(@PathVariable String userId) {
        return SuccessResponse.toOk(usersService.getUserOther(userId));
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<Void>> updateUser(
        @RequestHeader("X-User-Id")String userId,
        @RequestPart UsersUpdateRequest request,
        @RequestPart(required = false) MultipartFile file
    ) throws IOException {
        log.info("update user {}", request.toString());
        usersService.updateUser(userId, request, file);
        return SuccessResponse.toNoContent();
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<Void>> deleteUser(@PassportUser String userId){
        usersService.deleteUser(userId);
        return SuccessResponse.toNoContent();
    }

    @PatchMapping("/me")
    public ResponseEntity<SuccessResponse<Void>> updatePassword(
        @PassportUser String userId,
        @Valid @RequestBody PasswordUpdateRequest request
    ){
        log.info("update password {}", request.password());
        return SuccessResponse.toNoContent();
    }

    @GetMapping("/categories")
    public ResponseEntity<SuccessResponse<CategoryResponse>> getCategories(@PassportUser String userId) {
        return SuccessResponse.toOk(usersService.getCategories(userId));
    }

    @GetMapping("/categories/recommendations/{userId}")
    public List<String> getUserBooks(@PathVariable String userId){
        return usersService.getRecommendedCategories(userId);
    }
}
