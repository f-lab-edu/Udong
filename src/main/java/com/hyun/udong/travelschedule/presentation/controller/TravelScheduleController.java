package com.hyun.udong.travelschedule.presentation.controller;

import com.hyun.udong.common.annotation.LoginMember;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.travelschedule.application.service.TravelScheduleService;
import com.hyun.udong.travelschedule.domain.MemberTravelSchedule;
import com.hyun.udong.travelschedule.presentation.dto.TravelScheduleRequest;
import com.hyun.udong.travelschedule.presentation.dto.TravelScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel")
public class TravelScheduleController {

    private final TravelScheduleService travelScheduleService;

    @PostMapping("/schedule")
    public ResponseEntity<TravelScheduleResponse> updateTravelSchedule(
            @LoginMember Member member,
            @RequestBody TravelScheduleRequest travelScheduleRequest) {
        MemberTravelSchedule travelSchedule = travelScheduleService.updateTravelSchedule(member.getId(), travelScheduleRequest);
        TravelScheduleResponse response = TravelScheduleResponse.from(travelSchedule);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedule")
    public ResponseEntity<TravelScheduleResponse> getTravelSchedule(@LoginMember Member member) {
        MemberTravelSchedule travelSchedule = travelScheduleService.findTravelSchedule(member.getId());
        TravelScheduleResponse response = TravelScheduleResponse.from(travelSchedule);
        return ResponseEntity.ok(response);
    }
}
