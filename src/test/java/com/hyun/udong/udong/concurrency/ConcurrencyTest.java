package com.hyun.udong.udong.concurrency;

import com.hyun.udong.udong.application.service.UdongService;
import com.hyun.udong.udong.domain.*;
import com.hyun.udong.udong.infrastructure.repository.UdongRepository;
import com.hyun.udong.udong.infrastructure.repository.waitingmember.WaitingMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcurrencyTest {

    private static final int BIG_REQUEST = 1000;
    private static final int BIG_REQUEST_THREADS = Runtime.getRuntime().availableProcessors() * 4;
    private static final int SMALL_REQUEST = 10;
    private static final int SMALL_REQUEST_THREADS = 2;

    private Udong udong;

    @Autowired
    private WaitingMemberRepository waitingMemberRepository;

    @Autowired
    private UdongService udongService;

    @Autowired
    private UdongRepository udongRepository;

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

    private static void printRespoinseTimes(List<Long> responseTimes) {
        List<Long> responseTimeMillis = responseTimes.stream()
                .map(time -> time / 1_000_000)
                .toList();

        long fastest = responseTimeMillis.stream().min(Long::compare).orElse(0L);
        long slowest = responseTimeMillis.stream().max(Long::compare).orElse(0L);
        double average = responseTimeMillis.stream().mapToLong(Long::longValue).average().orElse(0);

        System.out.println("최단 응답 시간: " + fastest + "ms");
        System.out.println("최장 응답 시간: " + slowest + "ms");
        System.out.println("평균 응답 시간: " + average + "ms");
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
    void 대기자_리스트_초과_동시성_테스트_낙관적락_적은_충돌() throws InterruptedException {
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        // given
        final int REQUEST_MEMBER_COUNT = SMALL_REQUEST;
        ExecutorService executorService = Executors.newFixedThreadPool(SMALL_REQUEST_THREADS);
        CountDownLatch latch = new CountDownLatch(REQUEST_MEMBER_COUNT);

        long startTime = System.nanoTime();
        // when
        long startMemberId = 5L;
        for (int i = 0; i < REQUEST_MEMBER_COUNT; i++) {
            long memberId = startMemberId + i;
            executorService.submit(() -> {
                long requestStartTime = System.nanoTime();
                try {
                    udongService.requestParticipationWithOptimisticLock(udong.getId(), memberId);
                } finally {
                    long requestEndTime = System.nanoTime();
                    responseTimes.add(requestEndTime - requestStartTime);

                    latch.countDown();
                }
            });
        }

        latch.await(); // 스레드 완료될 때까지 대기
        executorService.shutdown();

        // then
        int waitingCount = waitingMemberRepository.countByUdong(udong);
        assertThat(waitingCount).isEqualTo(5);

        // 응답 시간
        long endTime = System.nanoTime();
        System.out.println("총 처리 시간(ms):" + (endTime - startTime));

        printRespoinseTimes(responseTimes);
    }

    @Test
    void 대기자_리스트_초과_동시성_테스트_낙관적락_많은_충돌() throws InterruptedException {
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        // given
        final int REQUEST_MEMBER_COUNT = BIG_REQUEST;
        ExecutorService executorService = Executors.newFixedThreadPool(BIG_REQUEST_THREADS);
        CountDownLatch latch = new CountDownLatch(REQUEST_MEMBER_COUNT);

        long startTime = System.nanoTime();
        // when
        long startMemberId = 5L;
        for (int i = 0; i < REQUEST_MEMBER_COUNT; i++) {
            long memberId = startMemberId + i;
            executorService.submit(() -> {
                long requestStartTime = System.nanoTime();
                try {
                    udongService.requestParticipationWithOptimisticLock(udong.getId(), memberId);
                } finally {
                    long requestEndTime = System.nanoTime();
                    responseTimes.add(requestEndTime - requestStartTime);

                    latch.countDown();
                }
            });
        }

        latch.await(); // 스레드 완료될 때까지 대기
        executorService.shutdown();

        // then
        int waitingCount = waitingMemberRepository.countByUdong(udong);
        assertThat(waitingCount).isEqualTo(5);

        // 응답 시간
        long endTime = System.nanoTime();
        System.out.println("총 처리 시간(ms):" + (endTime - startTime));

        printRespoinseTimes(responseTimes);
    }

    @Test
    void 대기자_리스트_초과_동시성_테스트_비관적락_적은_충돌() throws InterruptedException {
        System.out.println(">>>>>>>>>>>> ConcurrencyTest.대기자_리스트_초과_동시성_테스트_비관적락");
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        // given
        final int REQUEST_MEMBER_COUNT = SMALL_REQUEST;
        ExecutorService executorService = Executors.newFixedThreadPool(SMALL_REQUEST_THREADS);
        CountDownLatch latch = new CountDownLatch(REQUEST_MEMBER_COUNT);

        long startTime = System.nanoTime();

        // when
        long startMemberId = 5L;
        for (int i = 0; i < REQUEST_MEMBER_COUNT; i++) {
            long memberId = startMemberId + i;
            executorService.submit(() -> {
                long requestStartTime = System.nanoTime();
                try {
                    udongService.requestParticipationWithPessimisticLock(udong.getId(), memberId);
                } finally {
                    long requestEndTime = System.nanoTime();
                    responseTimes.add((requestEndTime - requestStartTime));

                    latch.countDown();
                }
            });
        }

        latch.await(); // 스레드 완료될 때까지 대기
        executorService.shutdown();

        // then
        int waitingCount = waitingMemberRepository.countByUdong(udong);
        assertThat(waitingCount).isEqualTo(5);

        // 응답 시간
        long endTime = System.nanoTime();
        System.out.println("총 처리 시간(ms):" + (endTime - startTime));

        printRespoinseTimes(responseTimes);
    }

    @Test
    void 대기자_리스트_초과_동시성_테스트_비관적락_많은_충돌() throws InterruptedException {
        System.out.println(">>>>>>>>>>>> ConcurrencyTest.대기자_리스트_초과_동시성_테스트_비관적락");
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        // given
        final int REQUEST_MEMBER_COUNT = BIG_REQUEST;
        ExecutorService executorService = Executors.newFixedThreadPool(BIG_REQUEST_THREADS);
        CountDownLatch latch = new CountDownLatch(REQUEST_MEMBER_COUNT);

        long startTime = System.nanoTime();

        // when
        long startMemberId = 5L;
        for (int i = 0; i < REQUEST_MEMBER_COUNT; i++) {
            long memberId = startMemberId + i;
            executorService.submit(() -> {
                long requestStartTime = System.nanoTime();
                try {
                    udongService.requestParticipationWithPessimisticLock(udong.getId(), memberId);
                } finally {
                    long requestEndTime = System.nanoTime();
                    responseTimes.add((requestEndTime - requestStartTime));

                    latch.countDown();
                }
            });
        }

        latch.await(); // 스레드 완료될 때까지 대기
        executorService.shutdown();

        // then
        int waitingCount = waitingMemberRepository.countByUdong(udong);
        assertThat(waitingCount).isEqualTo(5);

        // 응답 시간
        long endTime = System.nanoTime();
        System.out.println("총 처리 시간(ms):" + (endTime - startTime));

        printRespoinseTimes(responseTimes);
    }
}
