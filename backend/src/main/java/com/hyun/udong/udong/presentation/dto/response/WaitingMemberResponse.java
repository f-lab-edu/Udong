package com.hyun.udong.udong.presentation.dto.response;

import com.hyun.udong.udong.domain.WaitingMember;

import java.time.LocalDate;

public record WaitingMemberResponse(Long udongId, Long memberId, LocalDate requestDate) {

    public static WaitingMemberResponse of(WaitingMember member) {
        return new WaitingMemberResponse(
                member.getUdong().getId(),
                member.getMemberId(),
                member.getRequestDate());
    }
}
