package com.se.sealarm_be.setting.repository;

import com.se.sealarm_be.setting.domain.BoardMenuInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardMenuInfoRepository extends JpaRepository<BoardMenuInfo, Long> {

    List<BoardMenuInfo> findByMenuTypeOrderByDepthAscNameAsc(String menuType);
}
