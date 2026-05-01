package com.se.sealarm_be.messaging.publisher;

import com.se.sealarm_be.config.RedisConfig;
import com.se.sealarm_be.notification.controller.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(NotificationResponse notification) {
        try {
            String payload = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend(RedisConfig.BROADCAST_CHANNEL, payload);
        } catch (Exception e) {
            log.error("알림 Pub/Sub 발행 실패", e);
        }
    }
}
