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

    private static final int MAX_WAITING_COUNT = 5;
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

    @FunctionalInterface
    public interface TestExecutor {

        void execute(long memberId);
    }

    // 락 성능 테스트 템플릿 메서드
    void runConcurrentTest(int requestCount, int threadPoolSize, TestExecutor executor) throws InterruptedException {
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        // given
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(requestCount);

        long startTime = System.nanoTime();
        // when
        long startMemberId = 5L;
        for (int i = 0; i < requestCount; i++) {
            long memberId = startMemberId + i;
            executorService.submit(() -> {
                long requestStartTime = System.nanoTime();
                try {
                    executor.execute(memberId);
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
        assertThat(waitingCount).isEqualTo(MAX_WAITING_COUNT);

        // 응답 시간
        long endTime = System.nanoTime();
        System.out.println("총 처리 시간(ms):" + (endTime - startTime));

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
    void 낙관적락_적은_충돌() throws InterruptedException {
        runConcurrentTest(SMALL_REQUEST, SMALL_REQUEST_THREADS,
                memberId -> udongService.requestParticipationWithOptimisticLock(udong.getId(), memberId));
    }

    @Test
    void 낙관적락_많은_충돌() throws InterruptedException {
        runConcurrentTest(BIG_REQUEST, BIG_REQUEST_THREADS,
                memberId -> udongService.requestParticipationWithOptimisticLock(udong.getId(), memberId));
    }

    @Test
    void 비관적락_적은_충돌() throws InterruptedException {
        runConcurrentTest(SMALL_REQUEST, SMALL_REQUEST_THREADS,
                memberId -> udongService.requestParticipationWithPessimisticLock(udong.getId(), memberId));
    }

    @Test
    void 비관적락_많은_충돌() throws InterruptedException {
        runConcurrentTest(BIG_REQUEST, BIG_REQUEST_THREADS,
                memberId -> udongService.requestParticipationWithPessimisticLock(udong.getId(), memberId));
    }
}
