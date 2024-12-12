package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.domain.RefreshToken;
import com.cherrymango.foodiehub.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    // 전달 받은 리프레시 토큰으로 토큰 객체를 검색해서 전달
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new IllegalArgumentException("Unexpected token"));
    }

}
