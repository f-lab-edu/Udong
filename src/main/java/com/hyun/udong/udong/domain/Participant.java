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
public class Participant {

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(updatable = false, nullable = false)
    private LocalDate participationDate;

    @Builder
    public Participant(Long memberId) {
        this.memberId = memberId;
        this.participationDate = LocalDate.now();
    }
}
