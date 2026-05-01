package com.se.sealarm_be.setting.repository;

import com.se.sealarm_be.setting.domain.BoardMenuSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardMenuSubscriptionRepository extends JpaRepository<BoardMenuSubscription, Long> {

    List<BoardMenuSubscription> findByAccountId(Long accountId);

    List<BoardMenuSubscription> findByBoardMenuId(Long boardMenuId);

    Optional<BoardMenuSubscription> findByAccountIdAndBoardMenuId(Long accountId, Long boardMenuId);

    boolean existsByAccountIdAndBoardMenuId(Long accountId, Long boardMenuId);
}
