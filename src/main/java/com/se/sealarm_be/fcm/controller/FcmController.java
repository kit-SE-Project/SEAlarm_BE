package com.se.sealarm_be.fcm.controller;

import com.se.sealarm_be.fcm.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/token")
    public ResponseEntity<Void> registerToken(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        if (accountId == null) return ResponseEntity.status(401).build();
        fcmService.registerToken(accountId, body.get("token"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/token")
    public ResponseEntity<Void> deleteToken(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        if (accountId == null) return ResponseEntity.status(401).build();
        fcmService.deleteToken(accountId);
        return ResponseEntity.noContent().build();
    }
}
