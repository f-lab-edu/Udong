package com.hyun.udong.udong.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "udong_id", nullable = false)
    private Udong udong;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(updatable = false, nullable = false)
    private LocalDate participationDate;

    @Builder
    private Participant(Long memberId, Udong udong) {
        this.memberId = memberId;
        this.participationDate = LocalDate.now();
        this.udong = udong;
    }

    public static Participant from(Long memberId, Udong udong) {
        return new Participant(memberId, udong);
    }
}
