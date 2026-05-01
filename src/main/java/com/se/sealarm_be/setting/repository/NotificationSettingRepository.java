package com.se.sealarm_be.setting.repository;

import com.se.sealarm_be.notification.domain.NotificationType;
import com.se.sealarm_be.setting.domain.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    List<NotificationSetting> findByAccountId(Long accountId);

    Optional<NotificationSetting> findByAccountIdAndType(Long accountId, NotificationType type);
}
