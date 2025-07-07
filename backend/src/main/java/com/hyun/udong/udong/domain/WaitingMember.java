package com.hyun.udong.udong.domain;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "waiting_members")
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
    public WaitingMember(Udong udong, Long memberId) {
        this.udong = udong;
        this.memberId = memberId;
        this.requestDate = LocalDate.now();
    }

    public static WaitingMember of(Udong udong, Long memberId) {
        return WaitingMember.builder()
                .udong(udong)
                .memberId(memberId)
                .build();
    }

    @Override
    public String toString() {
        return "WaitingMember{" +
                ", udong=" + udong.getId() +
                ", memberId=" + memberId +
                '}';
    }
}
