package com.hyun.udong.udong.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Participants {

    private int currentParticipantsSize;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @ElementCollection
    @CollectionTable(name = "udong_member", joinColumns = @JoinColumn(name = "udong_id"))
    private Set<Participant> participants;

    private Participants(Long ownerId, Set<Participant> participants) {
        this.ownerId = ownerId;
        this.participants = participants;
        this.currentParticipantsSize = participants.size();
    }

    public static Participants from(Long ownerId) {
        Set<Participant> participants = Set.of(new Participant(ownerId));
        return new Participants(ownerId, participants);
    }

}
