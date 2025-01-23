package com.hyun.udong.auth.exception;

import com.hyun.udong.common.exception.ErrorCode;
import com.hyun.udong.common.exception.UdongException;

public class ExpiredTokenException extends UdongException {
    public static final ExpiredTokenException EXCEPTION = new ExpiredTokenException();

    private ExpiredTokenException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }
}
