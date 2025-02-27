package com.hyun.udong.udong.domain;

import com.hyun.udong.common.fixture.TestFixture;
import com.hyun.udong.udong.exception.InvalidParticipationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WaitingMemberTest {

    @Test
    void 대기자가_5명_이상일때_대기자를_생성하면_예외를_던진다() {
        assertThatThrownBy(() -> WaitingMember.of(TestFixture.UDONG1, 1L, 5))
                .isInstanceOf(InvalidParticipationException.class)
                .hasMessage("대기 인원이 초과되었습니다.");
    }
}
