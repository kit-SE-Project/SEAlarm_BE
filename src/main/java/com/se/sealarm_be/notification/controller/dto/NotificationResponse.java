package com.se.sealarm_be.notification.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.se.sealarm_be.notification.domain.Notification;
import com.se.sealarm_be.notification.domain.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private Long receiverId;
    private NotificationType type;
    private String title;
    private String content;
    private Long relatedId;
    @JsonProperty("isRead")
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.receiverId = notification.getReceiverId();
        this.type = notification.getType();
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.relatedId = notification.getRelatedId();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
    }
}
