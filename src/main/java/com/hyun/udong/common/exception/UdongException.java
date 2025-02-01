package com.hyun.udong.common.exception;

import org.springframework.http.HttpStatus;

public class UdongException extends RuntimeException {
    private final ErrorCode errorCode;

    public UdongException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public UdongException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }
}
