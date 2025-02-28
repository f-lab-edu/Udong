package com.hyun.udong.udong.concurrency;

import com.hyun.udong.udong.application.service.UdongService;
import com.hyun.udong.udong.domain.*;
import com.hyun.udong.udong.facade.OptimisticLockParticipationFacade;
import com.hyun.udong.udong.infrastructure.repository.UdongRepository;
import com.hyun.udong.udong.infrastructure.repository.waitingmember.WaitingMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcurrencyTest {

    private Udong udong;

    @Autowired
    private WaitingMemberRepository waitingMemberRepository;

    @Autowired
    private UdongService udongService;

    @Autowired
    private UdongRepository udongRepository;

    @Autowired
    private OptimisticLockParticipationFacade optimisticLockParticipationFacade;

    @BeforeEach
    void setUp() {
        System.out.println(">>>>>>>>>>>> setUp");
        udong = Udong.builder()
                .content(Content.of("여행 동행 모집", "즐겁게 여행할 동행을 구합니다."))
                .recruitPlanner(RecruitPlanner.from(5))
                .travelPlanner(TravelPlanner.of(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 5)))
                .attachedTags(AttachedTags.of(List.of("자연", "배낭여행")))
                .ownerId(1L)
                .build();
        udongRepository.save(udong);

        for (int i = 0; i < 4; i++) {
            waitingMemberRepository.save(WaitingMember.of(udong, (long) i));
        }
        udong.setCurrentWaitingMemberCount(4);
        udongRepository.save(udong);
    }

    @Test
    void 대기자_리스트_초과_동시성_테스트() throws InterruptedException {
        // given
        final int REQUEST_MEMBER_COUNT = 2;

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(REQUEST_MEMBER_COUNT);

        // when
        long startMemberId = 5L;
        for (int i = 0; i < REQUEST_MEMBER_COUNT; i++) {
            long memberId = startMemberId + i;
            executorService.submit(() -> {
                try {
                    udongService.requestParticipation(udong.getId(), memberId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 스레드 완료될 때까지 대기
        executorService.shutdown();

        // then
        int waitingCount = waitingMemberRepository.countByUdong(udong);
        assertThat(waitingCount).isEqualTo(6); // 5명이 제한인데, 6명이 대기자 리스트에 들어감
    }

    @Test
    void 대기자_리스트_초과_동시성_테스트_낙관적락() throws InterruptedException {
        System.out.println(">>>>>>>>>>>> ConcurrencyTest.대기자_리스트_초과_동시성_테스트_낙관적락");
        // given
        final int REQUEST_MEMBER_COUNT = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
        CountDownLatch latch = new CountDownLatch(REQUEST_MEMBER_COUNT);

        // when
        long startMemberId = 5L;
        for (int i = 0; i < REQUEST_MEMBER_COUNT; i++) {
            long memberId = startMemberId + i;
            executorService.submit(() -> {
                try {
                    optimisticLockParticipationFacade.requestParticipation(udong.getId(), memberId);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 스레드 완료될 때까지 대기
        executorService.shutdown();

        // then
        int waitingCount = waitingMemberRepository.countByUdong(udong);
        assertThat(waitingCount).isEqualTo(5);
    }
}
