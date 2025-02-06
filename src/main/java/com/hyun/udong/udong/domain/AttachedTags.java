package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidInputException;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class AttachedTags {

    @ElementCollection
    @CollectionTable(name = "udong_tags", joinColumns = @JoinColumn(name = "udong_id"))
    private Set<String> tags;

    private void validate(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            throw new InvalidInputException("태그는 비어있을 수 없습니다.");
        }

        if (tags.size() > 10) {
            throw new InvalidInputException("태그는 5개 이하로 설정해야 합니다.");
        }
    }

    public AttachedTags(Set<String> tags) {
        validate(tags);
        this.tags = tags;
    }

    public static AttachedTags of(List<String> tags) {
        return new AttachedTags(Set.copyOf(tags));
    }
}
