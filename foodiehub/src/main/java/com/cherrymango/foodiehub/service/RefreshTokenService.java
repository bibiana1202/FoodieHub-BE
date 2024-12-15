package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.config.jwt.TokenProvider;
import com.cherrymango.foodiehub.domain.RefreshToken;
import com.cherrymango.foodiehub.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    // 전달 받은 리프레시 토큰으로 토큰 객체를 검색해서 전달
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new IllegalArgumentException("Unexpected token"));
    }

    @Transactional
    public void delete(){
        // 현재 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체가 null이거나 credentials가 null인 경우 처리
        if (authentication == null || authentication.getCredentials() == null) {
            System.out.println("No credentials found for this authentication. Skipping refresh token deletion.");
            return; // 일반 로그인일 경우 아무 작업도 하지 않고 종료
        }

        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        Long userId = tokenProvider.getUserId(token);

        refreshTokenRepository.deleteByUserId(userId);
    }

}
