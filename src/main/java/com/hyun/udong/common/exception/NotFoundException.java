package com.hyun.udong.common.exception;

public class NotFoundException extends UdongException {
    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
