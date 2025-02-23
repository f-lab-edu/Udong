package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidParameterException;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class RecruitPlanner {

    private static final int MIN_RECRUIT_COUNT = 2;
    private static final int MAX_RECRUIT_COUNT = 10;

    private int recruitmentCount;

    private void validate(int recruitmentCount) {
        if (recruitmentCount < MIN_RECRUIT_COUNT || recruitmentCount > MAX_RECRUIT_COUNT) {
            throw new InvalidParameterException("모집 인원은 " + MIN_RECRUIT_COUNT + "명 이상 " + MAX_RECRUIT_COUNT + "명 이하로 설정해야 합니다.");
        }
    }

    private RecruitPlanner(int recruitmentCount) {
        validate(recruitmentCount);
        this.recruitmentCount = recruitmentCount;
    }

    public static RecruitPlanner from(int recruitmentCount) {
        return new RecruitPlanner(recruitmentCount);
    }

    public boolean isRecruitmentAvailable(int currentParticipantCount) {
        return currentParticipantCount < recruitmentCount;
    }
}
