package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByUserId(Long userId);

    @Transactional
    void deleteByRefreshToken(String refreshToken);  // 토큰 값을 기반으로 삭제


}