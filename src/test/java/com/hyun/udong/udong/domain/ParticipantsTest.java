package com.hyun.udong.udong.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipantsTest {

    @Test
    void 첫생성_시_모임장이_참가자로_자동등록된다() {
        Long ownerId = 1L;
        Participants participants = Participants.from(ownerId);

        assertThat(participants.getOwnerId()).isEqualTo(ownerId);
        assertThat(participants.getParticipants()).hasSize(1);
        assertThat(participants.getCurrentParticipantsSize()).isEqualTo(1);
    }
}
