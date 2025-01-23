package com.hyun.udong.travelschedule.exception;

import com.hyun.udong.common.exception.ErrorCode;
import com.hyun.udong.common.exception.UdongException;

public class CityNotFoundException extends UdongException {
    public static final CityNotFoundException EXCEPTION = new CityNotFoundException();

    private CityNotFoundException() {
        super(ErrorCode.CITY_NOT_FOUND);
    }
}
