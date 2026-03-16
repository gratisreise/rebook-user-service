package com.example.rebookuserservice.common.exception;

import com.rebook.common.core.exception.BusinessException;
import com.rebook.common.core.exception.ErrorCode;

public class UserException extends BusinessException {

    public UserException() {
        super(ErrorCode.UNKNOWN_ERROR);
    }

    // 존재하지 않는 유저
    public static UserException userNotFound() {
        return new UserException();
    }

    // 중복된 이메일
    public static UserException duplicatedEmail() {
        return new UserException();
    }

    // 중복된 닉네임
    public static UserException duplicatedNickname() {
        return new UserException();
    }

    // S3 이미지 업로드 실패
    public static UserException fileUploadFailed() {
        return new UserException();
    }

    // S3 이미지 URL 생성 실패
    public static UserException fileUrlGenerationFailed() {
        return new UserException();
    }
}
