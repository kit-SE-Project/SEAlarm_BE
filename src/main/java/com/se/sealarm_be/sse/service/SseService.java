package com.se.sealarm_be.sse.service;

import com.se.sealarm_be.sse.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    @Value("${sse.timeout}")
    private long sseTimeout;

    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter connect(Long accountId) {
        SseEmitter emitter = new SseEmitter(sseTimeout);

        emitter.onTimeout(() -> sseEmitterRepository.deleteByAccountId(accountId));
        emitter.onCompletion(() -> sseEmitterRepository.deleteByAccountId(accountId));
        emitter.onError(e -> sseEmitterRepository.deleteByAccountId(accountId));

        sseEmitterRepository.save(accountId, emitter);

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            sseEmitterRepository.deleteByAccountId(accountId);
        }

        return emitter;
    }

    public boolean isConnected(Long accountId) {
        return sseEmitterRepository.findByAccountId(accountId).isPresent();
    }

    public void send(Long accountId, Object data) {
        sseEmitterRepository.findByAccountId(accountId).ifPresent(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("notification").data(data));
            } catch (IOException e) {
                sseEmitterRepository.deleteByAccountId(accountId);
                log.warn("SSE 전송 실패 - accountId: {}", accountId);
            }
        });
    }

    // 30초마다 heartbeat 전송 - 연결 유지 + 끊긴 세션 즉시 감지
    @Scheduled(fixedDelay = 30000)
    public void sendHeartbeat() {
        Map<Long, SseEmitter> emitters = sseEmitterRepository.getAllEmitters();
        emitters.forEach((accountId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().comment("ping"));
            } catch (IOException e) {
                sseEmitterRepository.deleteByAccountId(accountId);
                log.debug("SSE heartbeat 실패 - 세션 제거: accountId={}", accountId);
            }
        });
    }
}
