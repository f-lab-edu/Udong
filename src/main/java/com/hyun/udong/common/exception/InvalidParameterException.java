package com.hyun.udong.common.exception;

public class InvalidParameterException extends UdongException {
    public InvalidParameterException(String message) {
        super(ErrorCode.INVALID_PARAMETER, message);
    }
}
