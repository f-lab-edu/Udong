package com.hyun.udong.travelschedule.exception;

import com.hyun.udong.common.exception.ErrorCode;
import com.hyun.udong.common.exception.UdongException;

public class TravelScheduleNotFoundException extends UdongException {
    public static final TravelScheduleNotFoundException EXCEPTION = new TravelScheduleNotFoundException();

    private TravelScheduleNotFoundException() {
        super(ErrorCode.MEMBER_TRAVEL_SCHEDULE_NOT_FOUND);
    }
}
