package com.hyun.udong.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
public enum ErrorCode {
    MEMBER_NOT_FOUND(BAD_REQUEST, "해당하는 회원이 없습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(UNAUTHORIZED, "만료된 토큰입니다."),
    UNAUTHENTICATED_MEMBER(UNAUTHORIZED, "인증되지 않은 회원입니다."),
    CITY_NOT_FOUND(BAD_REQUEST, "해당하는 도시가 없습니다."),
    MEMBER_TRAVEL_SCHEDULE_NOT_FOUND(BAD_REQUEST, "해당 회원의 여행 일정이 존재하지 않습니다."),
    NOT_FOUND(BAD_REQUEST);

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
