package com.hyun.udong.udong.presentation.dto.response;

import com.hyun.udong.common.annotation.DateFormat;
import com.hyun.udong.udong.domain.Udong;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Builder
public class SimpleUdongResponse {
    private final Long id;
    private final String title;
    private final String description;
    @DateFormat
    private final LocalDate startDate;
    @DateFormat
    private final LocalDate endDate;
    private final Set<String> tags;
    private final int currentParticipantsCount;
    private final int recruitmentCount;

    public static SimpleUdongResponse from(Udong udong, int currentParticipantsCount) {
        return new SimpleUdongResponse(udong.getId(),
                udong.getContent().getTitle(),
                udong.getContent().getDescription(),
                udong.getTravelPlanner().getStartDate(),
                udong.getTravelPlanner().getEndDate(),
                udong.getAttachedTags().getTags(),
                currentParticipantsCount,
                udong.getRecruitPlanner().getRecruitmentCount());
    }
}
