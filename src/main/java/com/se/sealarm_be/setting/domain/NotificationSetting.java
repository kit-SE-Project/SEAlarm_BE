package com.se.sealarm_be.setting.domain;

import com.se.sealarm_be.notification.domain.NotificationType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_settings",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "type"}))
@Getter
@NoArgsConstructor
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private boolean enabled;

    @Builder
    public NotificationSetting(Long accountId, NotificationType type, boolean enabled) {
        this.accountId = accountId;
        this.type = type;
        this.enabled = enabled;
    }

    public void updateEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
