package com.hyun.udong.udong.presentation.dto.response;

import com.hyun.udong.common.annotation.DateFormat;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.udong.domain.Udong;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Builder
public class CreateUdongResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final Set<String> tags;
    private final String status;
    private final Long ownerId;
    private final List<ParticipantResponse> participants;
    private final int currentParticipantsCount;
    @DateFormat
    private final LocalDate startDate;
    @DateFormat
    private final LocalDate endDate;
    @DateFormat
    private final LocalDateTime createdAt;

    public CreateUdongResponse(Long id,
                               String title,
                               String description,
                               Set<String> tags,
                               String status,
                               Long ownerId,
                               List<ParticipantResponse> participants,
                               int currentParticipantsCount,
                               LocalDate startDate,
                               LocalDate endDate,
                               LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.status = status;
        this.ownerId = ownerId;
        this.participants = participants;
        this.currentParticipantsCount = currentParticipantsCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    public static CreateUdongResponse from(Udong udong, List<Member> participants) {
        return new CreateUdongResponse(
                udong.getId(),
                udong.getContent().getTitle(),
                udong.getContent().getDescription(),
                udong.getAttachedTags().getTags(),
                udong.getStatus().name(),
                udong.getOwnerId(),
                participants.stream().map(ParticipantResponse::of).toList(),
                participants.size(),
                udong.getTravelPlanner().getStartDate(),
                udong.getTravelPlanner().getEndDate(),
                udong.getCreatedAt()
        );
    }
}
