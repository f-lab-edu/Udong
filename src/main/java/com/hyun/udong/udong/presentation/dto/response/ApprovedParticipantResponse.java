package com.hyun.udong.udong.presentation.dto.response;

import com.hyun.udong.udong.domain.Participant;

import java.time.LocalDate;

public record ApprovedParticipantResponse(Long udongId, Long memberId, LocalDate participationDate) {
    
    public static ApprovedParticipantResponse of(Participant participant) {
        return new ApprovedParticipantResponse(
                participant.getUdong().getId(),
                participant.getMemberId(),
                participant.getParticipationDate()
        );
    }
}
