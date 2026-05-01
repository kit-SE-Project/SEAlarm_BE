package com.se.sealarm_be.messaging.subscriber;

import com.se.sealarm_be.notification.controller.dto.NotificationResponse;
import com.se.sealarm_be.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationBroadcastSubscriber implements MessageListener {

    private final SseService sseService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            NotificationResponse notification =
                objectMapper.readValue(message.getBody(), NotificationResponse.class);
            sseService.send(notification.getReceiverId(), notification);
        } catch (Exception e) {
            log.error("SSE 브로드캐스트 처리 실패", e);
        }
    }
}
