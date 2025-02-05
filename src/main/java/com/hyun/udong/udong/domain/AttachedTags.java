package com.hyun.udong.udong.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class AttachedTags {

    @ElementCollection
    @CollectionTable(name = "udong_tags", joinColumns = @JoinColumn(name = "udong_id"))
    private Set<String> tags;

    @Builder
    public AttachedTags(Set<String> tags) {
        this.tags = tags;
    }
}
