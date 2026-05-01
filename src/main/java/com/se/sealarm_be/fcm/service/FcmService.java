package com.se.sealarm_be.fcm.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import com.se.sealarm_be.fcm.domain.FcmToken;
import com.se.sealarm_be.fcm.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void registerToken(Long accountId, String token) {
        fcmTokenRepository.findByAccountId(accountId)
            .ifPresentOrElse(
                fcmToken -> fcmToken.updateToken(token),
                () -> fcmTokenRepository.save(
                    FcmToken.builder().accountId(accountId).token(token).build()
                )
            );
    }

    @Transactional
    public void deleteToken(Long accountId) {
        fcmTokenRepository.deleteByAccountId(accountId);
    }

    public void sendNotification(Long accountId, String title, String body, Long relatedId) {
        if (FirebaseApp.getApps().isEmpty()) return;

        fcmTokenRepository.findByAccountId(accountId).ifPresent(fcmToken -> {
            try {
                Message message = Message.builder()
                    .setToken(fcmToken.getToken())
                    .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                    .putData("relatedId", relatedId != null ? String.valueOf(relatedId) : "")
                    .putData("url", relatedId != null ? "/posts/" + relatedId : "/")
                    .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                            .setIcon("/logo192.png")
                            .build())
                        .build())
                    .build();

                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                log.error("FCM 발송 실패 - accountId: {}", accountId, e);
                // 유효하지 않은 토큰이면 삭제
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
                    || e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                    fcmTokenRepository.deleteByAccountId(accountId);
                }
            }
        });
    }
}
