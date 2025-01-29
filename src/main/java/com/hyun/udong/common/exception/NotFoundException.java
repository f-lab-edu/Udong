package com.hyun.udong.common.exception;

public class NotFoundException extends UdongException {
    public NotFoundException(String message) {
        super(ErrorCode.MEMBER_TRAVEL_SCHEDULE_NOT_FOUND, message);
    }
}
