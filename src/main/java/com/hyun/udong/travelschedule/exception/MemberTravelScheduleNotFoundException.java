package com.hyun.udong.travelschedule.exception;

import com.hyun.udong.common.exception.ErrorCode;
import com.hyun.udong.common.exception.UdongException;

public class MemberTravelScheduleNotFoundException extends UdongException {
    public static final MemberTravelScheduleNotFoundException EXCEPTION = new MemberTravelScheduleNotFoundException();

    private MemberTravelScheduleNotFoundException() {
        super(ErrorCode.MEMBER_TRAVEL_SCHEDULE_NOT_FOUND);
    }
}
