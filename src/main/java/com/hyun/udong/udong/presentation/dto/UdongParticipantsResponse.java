package com.hyun.udong.udong.presentation.dto;

import com.hyun.udong.member.domain.Member;

import java.util.List;

public record UdongParticipantsResponse(int currentMemberCount, List<ParticipantResponse> participants) {

    public static UdongParticipantsResponse from(List<Member> participants) {
        return new UdongParticipantsResponse(
                participants.size(),
                participants.stream()
                        .map(ParticipantResponse::of)
                        .toList()
        );
    }
}


