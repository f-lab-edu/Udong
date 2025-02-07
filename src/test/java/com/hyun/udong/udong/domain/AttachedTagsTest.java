package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidParameterException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttachedTagsTest {

    @Test
    void 태그가_비어있으면_예외발생() {
        assertThatThrownBy(() -> AttachedTags.of(List.of()))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("태그는 비어있을 수 없습니다.");
    }

    @Test
    void 태그가_10개_초과되면_예외발생() {
        assertThatThrownBy(() -> AttachedTags.of(List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6")))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("태그는 5개 이하로 설정해야 합니다.");
    }
}
