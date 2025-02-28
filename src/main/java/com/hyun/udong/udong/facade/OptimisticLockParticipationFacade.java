package com.hyun.udong.udong.facade;

import com.hyun.udong.udong.application.service.UdongService;
import com.hyun.udong.udong.exception.InvalidParticipationException;
import com.hyun.udong.udong.presentation.dto.response.WaitingMemberResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OptimisticLockParticipationFacade {

    private static final int MAX_RETRY_COUNT = 5;

    private final UdongService udongService;

    public WaitingMemberResponse requestParticipation(Long udongId, Long memberId) throws InterruptedException {
        int retryCount = 0;

        for (int i = 1; i <= MAX_RETRY_COUNT; i++) {
            try {
                return udongService.requestParticipationWithLock(udongId, memberId);
            } catch (OptimisticLockException e) {
                log.warn("낙관적 락 충돌 발생! 재시도 중... (memberId: {}, retryCount: {})", memberId, retryCount);
                Thread.sleep(50);
            }
        }
        throw new InvalidParticipationException("최대 재시도 횟수 초과로 인해 참여 요청 실패 (memberId: " + memberId + ")");
    }
}

