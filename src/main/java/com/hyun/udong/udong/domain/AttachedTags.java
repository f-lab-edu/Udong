package com.hyun.udong.udong.domain;

import com.hyun.udong.common.exception.InvalidParameterException;
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

    public static final int MAX_TAGS_SIZE = 5;

    @ElementCollection
    @CollectionTable(name = "udong_tags", joinColumns = @JoinColumn(name = "udong_id"))
    private Set<String> tags;

    private void validate(Set<String> tags) {
        if (tags.size() > MAX_TAGS_SIZE) {
            throw new InvalidParameterException("태그는 " + MAX_TAGS_SIZE + "개 이하로 설정해야 합니다.");
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
