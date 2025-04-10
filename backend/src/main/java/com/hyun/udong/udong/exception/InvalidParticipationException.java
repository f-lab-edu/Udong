package com.hyun.udong.udong.exception;

import com.hyun.udong.common.exception.ErrorCode;
import com.hyun.udong.common.exception.UdongException;

public class InvalidParticipationException extends UdongException {
    public InvalidParticipationException(String message) {
        super(ErrorCode.INVALID_PARTICIPATION_REQUEST, message);
    }
}
