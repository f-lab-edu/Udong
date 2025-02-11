package com.hyun.udong.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
public enum ErrorCode {
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(UNAUTHORIZED, "만료된 토큰입니다."),
    UNAUTHENTICATED_MEMBER(UNAUTHORIZED, "인증되지 않은 회원입니다."),
    NOT_FOUND(BAD_REQUEST),
    INVALID_PARAMETER(BAD_REQUEST, "잘못된 입력값입니다."),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status) {
        this(status, null);
    }

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
