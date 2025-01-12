package com.hyun.udong.member.exception;

import com.hyun.udong.common.exception.ErrorCode;
import com.hyun.udong.common.exception.UdongException;

public class MemberNotFoundException extends UdongException {
    public static final UdongException EXCEPTION = new MemberNotFoundException();

    private MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
