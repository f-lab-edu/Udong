package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContentTest {

    @Test
    void 제목이_비어있다면_예외발생한다() {
        assertThatThrownBy(() -> Content.of("", "내용"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("제목은 1~100자 사이여야 합니다.");
    }

    @Test
    void 제목이_100자_초과면_예외발생한다() {
        String longTitle = "스위스 같이 가실 분".repeat(101);
        assertThatThrownBy(() -> Content.of(longTitle, "유효한 내용"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("제목은 1~100자 사이여야 합니다.");
    }

    @Test
    void 내용이_비어있으면_예외발생한다() {
        assertThatThrownBy(() -> Content.of("스위스 같이 가실 분", ""))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("내용은 1~1000자 사이여야 합니다.");
    }

    @Test
    void 내용이_1000자_초과면_예외발생한다() {
        String longDescription = "A".repeat(1001);
        assertThatThrownBy(() -> Content.of("스위스 같이 가실 분", longDescription))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("내용은 1~1000자 사이여야 합니다.");
    }
}
