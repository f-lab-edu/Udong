package com.hyun.udong.auth.exception;

import com.hyun.udong.common.exception.ErrorCode;
import com.hyun.udong.common.exception.UdongException;

public class UnAuthenticatedMemberException extends UdongException {
    public static final UdongException EXCEPTION = new UnAuthenticatedMemberException();

    private UnAuthenticatedMemberException() {
        super(ErrorCode.UNAUTHENTICATED_MEMBER);
    }
}
