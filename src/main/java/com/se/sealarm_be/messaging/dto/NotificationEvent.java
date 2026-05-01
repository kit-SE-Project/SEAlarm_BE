package com.se.sealarm_be.messaging.dto;

import com.se.sealarm_be.notification.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private NotificationType type;
    private Long receiverId;
    private String actorName;
    private Long relatedId;
    private String title;
    private String content;
    private Long boardMenuId;
}
