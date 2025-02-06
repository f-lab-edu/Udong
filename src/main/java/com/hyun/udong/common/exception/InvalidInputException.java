package com.hyun.udong.common.exception;

public class InvalidInputException extends UdongException {
    public InvalidInputException(String message) {
        super(ErrorCode.INVALID_INPUT, message);
    }
}
