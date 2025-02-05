package com.hyun.udong.udong.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class WaitingMembers {

    @ElementCollection
    @CollectionTable(name = "udong_waiting_member", joinColumns = @JoinColumn(name = "udong_id"))
    private Set<WaitingMember> waitingMembers;
}
