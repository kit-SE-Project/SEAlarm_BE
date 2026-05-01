package com.se.sealarm_be.setting.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_menu_subscriptions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "board_menu_id"}))
@Getter
@NoArgsConstructor
public class BoardMenuSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "board_menu_id", nullable = false)
    private Long boardMenuId;

    @Builder
    public BoardMenuSubscription(Long accountId, Long boardMenuId) {
        this.accountId = accountId;
        this.boardMenuId = boardMenuId;
    }
}
