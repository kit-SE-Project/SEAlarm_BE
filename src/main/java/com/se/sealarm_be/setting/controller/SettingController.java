package com.se.sealarm_be.setting.controller;

import com.se.sealarm_be.notification.domain.NotificationType;
import com.se.sealarm_be.setting.domain.BoardMenuInfo;
import com.se.sealarm_be.setting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    public ResponseEntity<Map<NotificationType, Boolean>> getSettings(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        return ResponseEntity.ok(settingService.getSettings(accountId));
    }

    @PatchMapping("/{type}")
    public ResponseEntity<Void> updateSetting(
            @PathVariable NotificationType type,
            @RequestParam boolean enabled,
            @AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        settingService.updateSetting(accountId, type, enabled);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/boards/available")
    public ResponseEntity<List<BoardMenuInfo>> getAvailableBoards() {
        return ResponseEntity.ok(settingService.getAvailableBoards());
    }

    @GetMapping("/boards")
    public ResponseEntity<List<Long>> getSubscribedBoards(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        return ResponseEntity.ok(settingService.getSubscribedBoards(accountId));
    }

    @PostMapping("/boards/{menuId}")
    public ResponseEntity<Void> subscribe(
            @PathVariable Long menuId,
            @AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        settingService.subscribe(accountId, menuId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/boards/{menuId}")
    public ResponseEntity<Void> unsubscribe(
            @PathVariable Long menuId,
            @AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        settingService.unsubscribe(accountId, menuId);
        return ResponseEntity.noContent().build();
    }
}
