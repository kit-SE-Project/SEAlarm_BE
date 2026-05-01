package com.se.sealarm_be.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FirebaseConfig {

    // prod: K8s Secret으로 주입
    @Value("${firebase.service-account-json:}")
    private String serviceAccountJson;

    // local: 파일 경로 fallback
    @Value("${firebase.service-account-path:firebase/serviceAccountKey.json}")
    private String serviceAccountPath;

    @PostConstruct
    public void initialize() {
        if (!FirebaseApp.getApps().isEmpty()) return;

        try {
            InputStream stream = resolveCredentials();
            if (stream == null) {
                log.warn("Firebase 자격증명 없음 - FCM 비활성화");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(stream))
                .build();

            FirebaseApp.initializeApp(options);
            log.info("Firebase 초기화 완료");
        } catch (IOException e) {
            log.warn("Firebase 초기화 실패: {}", e.getMessage());
        }
    }

    private InputStream resolveCredentials() throws IOException {
        // 1순위: 환경변수 (prod)
        if (serviceAccountJson != null && !serviceAccountJson.isBlank()) {
            return new ByteArrayInputStream(
                serviceAccountJson.getBytes(StandardCharsets.UTF_8));
        }
        // 2순위: classpath 파일 (local)
        try {
            return new ClassPathResource(serviceAccountPath).getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
}
