package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.common.config.QueryDslConfig;
import com.hyun.udong.common.fixture.TestFixture;
import com.hyun.udong.udong.domain.Participant;
import com.hyun.udong.udong.domain.Udong;
import com.hyun.udong.udong.presentation.dto.response.ParticipantCountResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@Import(QueryDslConfig.class)
class ParticipantRepositoryImplTest {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UdongRepository udongRepository;

    @Test
    void 우동의_참가자_수를_조회한다() {
        // given
        Udong udong1 = udongRepository.save(TestFixture.UDONG1);
        Udong udong2 = udongRepository.save(TestFixture.UDONG2);

        List<Participant> participants = List.of(
                Participant.builder().udong(udong1).memberId(1L).build(),
                Participant.builder().udong(udong2).memberId(2L).build(),
                Participant.builder().udong(udong2).memberId(3L).build()
        );
        participantRepository.saveAll(participants);

        // when
        List<ParticipantCountResponse> responses = participantRepository.countParticipantsByUdongIds(List.of(udong1.getId(), udong2.getId()));

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(ParticipantCountResponse::udongId, ParticipantCountResponse::participantCount)
                .containsExactlyInAnyOrder(
                        tuple(udong1.getId(), 1),
                        tuple(udong2.getId(), 2)
                );
    }
}
