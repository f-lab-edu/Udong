package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidParameterException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Content {

    private static final int MIN_TITLE_SIZE = 1;
    private static final int MAX_TITLE_SIZE = 100;
    private static final int MIN_DESCRIPTION_SIZE = 1;
    private static final int MAX_DESCRIPTION_SIZE = 1000;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private void validate(String title, String description) {
        if (title.length() > MAX_TITLE_SIZE) {
            throw new InvalidParameterException("제목은 " + MIN_TITLE_SIZE + "~" + MAX_TITLE_SIZE + "자 사이여야 합니다.");
        }
        if (description.length() > MAX_DESCRIPTION_SIZE) {
            throw new InvalidParameterException("내용은 " + MIN_DESCRIPTION_SIZE + "~" + MAX_DESCRIPTION_SIZE + "자 사이여야 합니다.");
        }
    }

    private Content(String title, String description) {
        validate(title, description);
        this.title = title;
        this.description = description;
    }

    public static Content of(String title, String description) {
        return new Content(title, description);
    }
}
