package com.hyun.udong.travelschedule.presentation.controller;

import com.hyun.udong.common.annotation.LoginMember;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.travelschedule.application.service.TravelScheduleService;
import com.hyun.udong.travelschedule.domain.TravelSchedule;
import com.hyun.udong.travelschedule.presentation.dto.TravelScheduleRequest;
import com.hyun.udong.travelschedule.presentation.dto.TravelScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel")
public class TravelScheduleController {

    private final TravelScheduleService travelScheduleService;

    @PostMapping("/schedule")
    public ResponseEntity<TravelScheduleResponse> updateTravelSchedule(
            @LoginMember Member member,
            @RequestBody TravelScheduleRequest travelScheduleRequest) {
        TravelSchedule travelSchedule = travelScheduleService.updateTravelSchedule(member.getId(), travelScheduleRequest);
        TravelScheduleResponse response = TravelScheduleResponse.from(travelSchedule);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedule")
    public ResponseEntity<TravelScheduleResponse> getTravelSchedule(@LoginMember Member member) {
        TravelSchedule travelSchedule = travelScheduleService.findTravelSchedule(member.getId());
        TravelScheduleResponse response = TravelScheduleResponse.from(travelSchedule);
        return ResponseEntity.ok(response);
    }
}
