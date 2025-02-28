package com.hyun.udong.udong.domain;

import com.hyun.udong.udong.exception.InvalidParticipationException;
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

    private static final int MAX_WAITING_COUNT = 5;

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
        validateWaitingCount(udong.getCurrentWaitingMemberCount());
        return WaitingMember.builder()
                .udong(udong)
                .memberId(memberId)
                .build();
    }

    private static void validateWaitingCount(int currentWaitingMembersCount) {
        if (currentWaitingMembersCount >= MAX_WAITING_COUNT) {
            throw new InvalidParticipationException("대기 인원이 초과되었습니다.");
        }
    }

    @Override
    public String toString() {
        return "WaitingMember{" +
                ", udong=" + udong.getId() +
                ", memberId=" + memberId +
                '}';
    }
}
