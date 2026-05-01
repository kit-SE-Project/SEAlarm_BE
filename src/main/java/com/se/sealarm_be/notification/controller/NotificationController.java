package com.se.sealarm_be.notification.controller;

import com.se.sealarm_be.notification.controller.dto.NotificationResponse;
import com.se.sealarm_be.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// accountId claim이 없는 구 토큰 대비: 재로그인 필요

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20) Pageable pageable) {
        Long accountId = jwt.getClaim("accountId");
        return ResponseEntity.ok(notificationService.getNotifications(accountId, pageable));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(accountId)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        notificationService.markAsRead(id, accountId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        notificationService.markAllAsRead(accountId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        notificationService.deleteOne(id, accountId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        notificationService.deleteAll(accountId);
        return ResponseEntity.noContent().build();
    }
}
