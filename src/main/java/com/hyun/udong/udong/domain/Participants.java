package com.hyun.udong.udong.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Participants {

    @Column(name = "current_member_count")
    private int size;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @ElementCollection
    @CollectionTable(name = "udong_member", joinColumns = @JoinColumn(name = "udong_id"))
    private Set<Participant> participants = new HashSet<>();
}
