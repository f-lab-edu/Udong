package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecruitPlannerTest {

    @Test
    void 모집인원이_2명_미만이면_예외발생() {
        assertThatThrownBy(() -> RecruitPlanner.from(1))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("모집 인원은 2명 이상 10명 이하로 설정해야 합니다.");
    }

    @Test
    void 모집인원이_10명_초과면_예외발생() {
        assertThatThrownBy(() -> RecruitPlanner.from(11))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("모집 인원은 2명 이상 10명 이하로 설정해야 합니다.");
    }

    @Test
    void 모집인원이_정상범위면_생성성공() {
        RecruitPlanner recruitPlanner = RecruitPlanner.from(5);

        assertThat(recruitPlanner.getRecruitmentCount()).isEqualTo(5);
    }
}
