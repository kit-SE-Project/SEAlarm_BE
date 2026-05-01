package com.se.sealarm_be.setting.service;

import com.se.sealarm_be.notification.domain.NotificationType;
import com.se.sealarm_be.setting.domain.BoardMenuInfo;
import com.se.sealarm_be.setting.domain.BoardMenuSubscription;
import com.se.sealarm_be.setting.domain.NotificationSetting;
import com.se.sealarm_be.setting.repository.BoardMenuInfoRepository;
import com.se.sealarm_be.setting.repository.BoardMenuSubscriptionRepository;
import com.se.sealarm_be.setting.repository.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final NotificationSettingRepository settingRepository;
    private final BoardMenuSubscriptionRepository subscriptionRepository;
    private final BoardMenuInfoRepository boardMenuInfoRepository;

    // 타입별 설정 전체 조회 (DB에 없는 타입은 기본값 true)
    @Transactional(readOnly = true)
    public Map<NotificationType, Boolean> getSettings(Long accountId) {
        Map<NotificationType, Boolean> saved = settingRepository.findByAccountId(accountId)
            .stream()
            .collect(Collectors.toMap(NotificationSetting::getType, NotificationSetting::isEnabled));

        return Arrays.stream(NotificationType.values())
            .collect(Collectors.toMap(
                type -> type,
                type -> saved.getOrDefault(type, true)
            ));
    }

    // 특정 타입 수신 여부 확인 (이벤트 처리 시 사용)
    @Transactional(readOnly = true)
    public boolean isEnabled(Long accountId, NotificationType type) {
        return settingRepository.findByAccountIdAndType(accountId, type)
            .map(NotificationSetting::isEnabled)
            .orElse(true);
    }

    // 타입별 설정 변경
    @Transactional
    public void updateSetting(Long accountId, NotificationType type, boolean enabled) {
        NotificationSetting setting = settingRepository
            .findByAccountIdAndType(accountId, type)
            .orElseGet(() -> NotificationSetting.builder()
                .accountId(accountId)
                .type(type)
                .enabled(true)
                .build());

        setting.updateEnabled(enabled);
        settingRepository.save(setting);
    }

    // 구독 게시판 목록 조회
    @Transactional(readOnly = true)
    public List<Long> getSubscribedBoards(Long accountId) {
        return subscriptionRepository.findByAccountId(accountId)
            .stream()
            .map(BoardMenuSubscription::getBoardMenuId)
            .toList();
    }

    // 게시판 구독
    @Transactional
    public void subscribe(Long accountId, Long boardMenuId) {
        if (!subscriptionRepository.existsByAccountIdAndBoardMenuId(accountId, boardMenuId)) {
            subscriptionRepository.save(
                BoardMenuSubscription.builder()
                    .accountId(accountId)
                    .boardMenuId(boardMenuId)
                    .build()
            );
        }
    }

    // 게시판 구독 취소
    @Transactional
    public void unsubscribe(Long accountId, Long boardMenuId) {
        subscriptionRepository.findByAccountIdAndBoardMenuId(accountId, boardMenuId)
            .ifPresent(subscriptionRepository::delete);
    }

    // 전체 게시판 목록 조회 (BOARD 타입만)
    @Transactional(readOnly = true)
    public List<BoardMenuInfo> getAvailableBoards() {
        return boardMenuInfoRepository.findByMenuTypeOrderByDepthAscNameAsc("BOARD");
    }

    // 특정 게시판 구독자 accountId 목록 (NEW_POST 팬아웃용)
    @Transactional(readOnly = true)
    public List<Long> getSubscribersByBoardMenuId(Long boardMenuId) {
        return subscriptionRepository.findByBoardMenuId(boardMenuId)
            .stream()
            .map(BoardMenuSubscription::getAccountId)
            .toList();
    }
}
