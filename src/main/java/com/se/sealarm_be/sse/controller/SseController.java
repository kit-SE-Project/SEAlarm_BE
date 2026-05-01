package com.se.sealarm_be.sse.controller;

import com.se.sealarm_be.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<?> connect(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("accountId");
        if (accountId == null) {
            return ResponseEntity.status(401).body("토큰을 재발급 받아주세요. 다시 로그인해주세요.");
        }
        return ResponseEntity.ok(sseService.connect(accountId));
    }
}
