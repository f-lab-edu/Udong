package com.hyun.udong.udong.presentation.dto;

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
public class UdongResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final int currentMemberCount;
    private final Set<String> tags;
    private final String status;
    private final Long ownerId;
    private final List<ParticipantResponse> participants;
    @DateFormat
    private final LocalDate startDate;
    @DateFormat
    private final LocalDate endDate;
    @DateFormat
    private final LocalDateTime createdAt;

    public UdongResponse(Long id,
                         String title,
                         String description,
                         Set<String> tags,
                         String status,
                         Long ownerId,
                         List<ParticipantResponse> participants,
                         int currentMemberCount,
                         LocalDate startDate,
                         LocalDate endDate,
                         LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.currentMemberCount = currentMemberCount;
        this.tags = tags;
        this.status = status;
        this.ownerId = ownerId;
        this.participants = participants;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    public static UdongResponse from(Udong udong, List<Member> participants) {
        return new UdongResponse(
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
