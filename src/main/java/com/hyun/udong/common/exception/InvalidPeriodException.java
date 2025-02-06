package com.hyun.udong.common.exception;

public class InvalidPeriodException extends UdongException {
    public InvalidPeriodException(String message) {
        super(ErrorCode.INVALID_PERIOD, message);
    }
}
