package com.hyun.udong.udong.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class WaitingMember {

    @Column(nullable = false)
    private Long memberId;

    @Column(updatable = false, nullable = false)
    private LocalDate requestDate;

    @Builder
    public WaitingMember(Long memberId) {
        this.memberId = memberId;
        this.requestDate = LocalDate.now();
    }
}
