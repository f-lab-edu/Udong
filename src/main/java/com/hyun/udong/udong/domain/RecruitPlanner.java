package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidParameterException;
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

    private void validate(int recruitmentCount) {
        if (recruitmentCount < 2 || recruitmentCount > 10) {
            throw new InvalidParameterException("모집 인원은 2명 이상 10명 이하로 설정해야 합니다.");
        }
    }

    private RecruitPlanner(int recruitmentCount) {
        validate(recruitmentCount);
        this.recruitmentCount = recruitmentCount;
    }

    public static RecruitPlanner from(int recruitmentCount) {
        return new RecruitPlanner(recruitmentCount);
    }

}
