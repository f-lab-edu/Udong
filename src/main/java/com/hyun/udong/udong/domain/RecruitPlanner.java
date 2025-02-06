package com.hyun.udong.udong.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class RecruitPlanner {

    @Column(name = "recruitment_count")
    private int recruitmentCount;

    private RecruitPlanner(int recruitmentCount) {
        this.recruitmentCount = recruitmentCount;
    }

    public static RecruitPlanner of(int recruitmentCount) {
        return new RecruitPlanner(recruitmentCount);
    }

}
