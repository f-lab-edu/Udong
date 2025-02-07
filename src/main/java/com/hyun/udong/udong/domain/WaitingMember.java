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
public class WaitingMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waiting_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "udong_id", nullable = false)
    private Udong udong;

    @Column(nullable = false)
    private Long memberId;

    @Column(updatable = false, nullable = false)
    private LocalDate requestDate;

    @Builder
    public WaitingMember(Long memberId) {
        this.memberId = memberId;
        this.requestDate = LocalDate.now();
    }

    @Builder
    public WaitingMember(Udong udong, Long memberId) {
        this.udong = udong;
        this.memberId = memberId;
        this.requestDate = LocalDate.now();
    }
}
