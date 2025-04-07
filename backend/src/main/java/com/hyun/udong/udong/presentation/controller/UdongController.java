package com.hyun.udong.udong.presentation.controller;

import com.hyun.udong.common.annotation.LoginMember;
import com.hyun.udong.common.dto.PagedResponse;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.udong.application.service.UdongService;
import com.hyun.udong.udong.presentation.dto.request.CreateUdongRequest;
import com.hyun.udong.udong.presentation.dto.request.FindUdongsCondition;
import com.hyun.udong.udong.presentation.dto.response.ApprovedParticipantResponse;
import com.hyun.udong.udong.presentation.dto.response.CreateUdongResponse;
import com.hyun.udong.udong.presentation.dto.response.SimpleUdongResponse;
import com.hyun.udong.udong.presentation.dto.response.WaitingMemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/udongs")
public class UdongController {
    private final UdongService udongService;

    @PostMapping
    public ResponseEntity<CreateUdongResponse> createUdong(@Valid @RequestBody CreateUdongRequest request,
                                                           @LoginMember Member member) {
        return ResponseEntity.ok(udongService.createUdong(request, member.getId()));
    }

    @GetMapping
    public PagedResponse<SimpleUdongResponse> getUdongs(@Valid @ModelAttribute FindUdongsCondition request,
                                                        Pageable pageable,
                                                        @LoginMember Member member) {
        return udongService.findUdongs(request, pageable);
    }

    @PostMapping("/{udongId}/participate")
    public ResponseEntity<WaitingMemberResponse> participantUdong(@PathVariable("udongId") Long udongId,
                                                                  @LoginMember Member member) {
        return ResponseEntity.ok(udongService.requestParticipation(udongId, member.getId()));
    }

    @PostMapping("/{udongId}/approve/{waitingMemberId}")
    public ResponseEntity<ApprovedParticipantResponse> approveParticipant(@PathVariable("udongId") Long udongId,
                                                                          @PathVariable("waitingMemberId") Long waitingMemberId,
                                                                          @LoginMember Member member) {
        ApprovedParticipantResponse response = udongService.approveParticipant(udongId, waitingMemberId, member.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{udongId}/reject/{waitingMemberId}")
    public ResponseEntity<Void> rejectParticipant(@PathVariable("udongId") Long udongId,
                                                  @PathVariable("waitingMemberId") Long waitingMemberId,
                                                  @LoginMember Member member) {
        udongService.rejectParticipant(udongId, waitingMemberId, member.getId());
        return ResponseEntity.noContent().build();
    }
}
