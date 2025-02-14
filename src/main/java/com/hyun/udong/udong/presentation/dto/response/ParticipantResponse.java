package com.hyun.udong.udong.presentation.dto.response;

import com.hyun.udong.member.domain.Member;

public record ParticipantResponse(Long id, String name, String profileImage) {

    public static ParticipantResponse of(Member member) {
        return new ParticipantResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }
}
