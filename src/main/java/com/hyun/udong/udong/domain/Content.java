package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidInputException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Content {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private void validate(String title, String description) {
        if (title == null || title.isBlank() || title.length() > 100) {
            throw new InvalidInputException("제목은 1~100자 사이여야 합니다.");
        }
        if (description == null || description.isBlank() || description.length() > 1000) {
            throw new InvalidInputException("내용은 1~1000자 사이여야 합니다.");
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
