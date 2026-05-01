package com.se.sealarm_be.messaging.listener;

import com.se.sealarm_be.config.RedisConfig;
import com.se.sealarm_be.messaging.dto.NotificationEvent;
import com.se.sealarm_be.messaging.publisher.NotificationPublisher;
import com.se.sealarm_be.notification.controller.dto.NotificationResponse;
import com.se.sealarm_be.notification.domain.Notification;
import com.se.sealarm_be.notification.service.NotificationService;
import com.se.sealarm_be.fcm.service.FcmService;
import com.se.sealarm_be.setting.service.SettingService;
import com.se.sealarm_be.notification.domain.NotificationType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final RedisTemplate<String, String> redisTemplate;
    private final StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamListenerContainer;
    private final NotificationService notificationService;
    private final NotificationPublisher notificationPublisher;
    private final SettingService settingService;
    private final FcmService fcmService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        try {
            redisTemplate.opsForStream().add(
                RedisConfig.NOTIFICATION_STREAM, java.util.Map.of("init", "true"));
        } catch (Exception ignored) {}

        try {
            redisTemplate.opsForStream().createGroup(
                RedisConfig.NOTIFICATION_STREAM, RedisConfig.CONSUMER_GROUP);
        } catch (Exception ignored) {}

        streamListenerContainer.receive(
            Consumer.from(RedisConfig.CONSUMER_GROUP, "consumer-" + UUID.randomUUID()),
            StreamOffset.create(RedisConfig.NOTIFICATION_STREAM, ReadOffset.lastConsumed()),
            this
        );
        streamListenerContainer.start();
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            String payload = message.getValue().get("event");
            if (payload == null) {
                ack(message);
                return;
            }

            NotificationEvent event = objectMapper.readValue(payload, NotificationEvent.class);

            if (event.getReceiverId() != null) {
                processSingleNotification(event);
            } else if (event.getBoardMenuId() != null) {
                processBroadcastNotification(event);
            }

            ack(message);
        } catch (Exception e) {
            log.error("알림 이벤트 처리 실패 - messageId: {}", message.getId(), e);
        }
    }

    private void processSingleNotification(NotificationEvent event) {
        NotificationType type = event.getType();
        if (!settingService.isEnabled(event.getReceiverId(), type)) return;

        Notification saved = notificationService.save(
            event.getReceiverId(), type,
            event.getTitle(), event.getContent(), event.getRelatedId()
        );
        notificationPublisher.publish(new NotificationResponse(saved));
        // 항상 FCM 발송 - 포그라운드면 onMessage 핸들러가 조용히 처리, 백그라운드면 OS 알림 표시
        fcmService.sendNotification(event.getReceiverId(), event.getTitle(), event.getContent(), event.getRelatedId());
    }

    private void processBroadcastNotification(NotificationEvent event) {
        List<Long> subscribers = settingService.getSubscribersByBoardMenuId(event.getBoardMenuId());
        NotificationType type = event.getType();

        for (Long receiverId : subscribers) {
            if (!settingService.isEnabled(receiverId, type)) continue;

            Notification saved = notificationService.save(
                receiverId, type,
                event.getTitle(), event.getContent(), event.getRelatedId()
            );
            notificationPublisher.publish(new NotificationResponse(saved));
            fcmService.sendNotification(receiverId, event.getTitle(), event.getContent(), event.getRelatedId());
        }
    }

    private void ack(MapRecord<String, String, String> message) {
        redisTemplate.opsForStream().acknowledge(
            RedisConfig.NOTIFICATION_STREAM, RedisConfig.CONSUMER_GROUP, message.getId());
    }
}
