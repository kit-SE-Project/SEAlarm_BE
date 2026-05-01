package com.se.sealarm_be.sse.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long accountId, SseEmitter emitter) {
        emitters.put(accountId, emitter);
        return emitter;
    }

    public Optional<SseEmitter> findByAccountId(Long accountId) {
        return Optional.ofNullable(emitters.get(accountId));
    }

    public void deleteByAccountId(Long accountId) {
        emitters.remove(accountId);
    }

    public Map<Long, SseEmitter> getAllEmitters() {
        return Map.copyOf(emitters);
    }
}
