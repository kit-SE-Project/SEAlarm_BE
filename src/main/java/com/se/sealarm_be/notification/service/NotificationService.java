package com.se.sealarm_be.notification.service;

import com.se.sealarm_be.notification.controller.dto.NotificationResponse;
import com.se.sealarm_be.notification.domain.Notification;
import com.se.sealarm_be.notification.domain.NotificationType;
import com.se.sealarm_be.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(Long accountId, Pageable pageable) {
        return notificationRepository
            .findByReceiverIdOrderByCreatedAtDesc(accountId, pageable)
            .map(NotificationResponse::new);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long accountId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(accountId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long accountId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        if (!notification.getReceiverId().equals(accountId)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        notification.markAsRead();
    }

    @Transactional
    public void markAllAsRead(Long accountId) {
        notificationRepository.markAllAsRead(accountId);
    }

    @Transactional
    public void deleteOne(Long notificationId, Long accountId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        if (!notification.getReceiverId().equals(accountId)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        notificationRepository.delete(notification);
    }

    @Transactional
    public void deleteAll(Long accountId) {
        notificationRepository.deleteAllByReceiverId(accountId);
    }

    @Transactional
    public Notification save(Long receiverId, NotificationType type, String title, String content, Long relatedId) {
        return notificationRepository.save(
            Notification.builder()
                .receiverId(receiverId)
                .type(type)
                .title(title)
                .content(content)
                .relatedId(relatedId)
                .build()
        );
    }
}
