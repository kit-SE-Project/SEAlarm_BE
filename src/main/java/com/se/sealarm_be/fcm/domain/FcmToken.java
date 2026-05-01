package com.se.sealarm_be.fcm.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fcm_tokens",
    uniqueConstraints = @UniqueConstraint(columnNames = "account_id"))
@Getter
@NoArgsConstructor
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(nullable = false, length = 500)
    private String token;

    @Builder
    public FcmToken(Long accountId, String token) {
        this.accountId = accountId;
        this.token = token;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
