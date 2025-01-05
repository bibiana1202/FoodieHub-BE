package com.cherrymango.foodiehub.util;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.service.SiteUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class TokenUtil {

    private final JwtUtil jwtUtil;
    private final SiteUserService siteUserService;

    /**
     * HttpServletRequest를 받아 JWT를 검증하고 SiteUser를 반환하는 메서드
     *
     * @param request HttpServletRequest 객체
     * @return SiteUser 사용자 객체
     * @throws IllegalArgumentException 유효하지 않은 토큰 또는 사용자 정보를 찾을 수 없을 경우
     */
    public SiteUser getSiteUserFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // Authorization 헤더 확인
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header missing or invalid");
        }

        // Bearer 토큰 값 추출
        String token = authHeader.substring(7);

        try {
            // JWT에서 사용자 정보 추출
            String email = jwtUtil.extractEmail(token);
            Long userId = jwtUtil.extractUserId(token);

            // 유저 ID로 SiteUser 조회
            SiteUser siteUser = siteUserService.findById(userId);

            // 유효성 확인
            if (siteUser == null || !siteUser.getEmail().equals(email)) {
                throw new IllegalArgumentException("User not found or email mismatch");
            }

            return siteUser;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired token", e);
        }
    }

    public Long getSiteUserIdOrThrow(Principal principal, HttpServletRequest request) {
        if (principal instanceof OAuth2AuthenticationToken) {
            // OAuth2 인증 방식 처리
            SiteUser user = getSiteUserFromRequest(request);
            return user.getId();
        } else if (principal != null) {
            // 일반 인증 방식 처리
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                throw new IllegalArgumentException("Authentication object is null");
            }
            String email = authentication.getName();
            return siteUserService.findByEmail(email)
                    .map(SiteUser::getId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        } else {
            throw new IllegalArgumentException("Principal is null or invalid");
        }
    }

    public Long getSiteUserIdOrNull(Principal principal, HttpServletRequest request) {
        if (principal instanceof OAuth2AuthenticationToken) {
            // OAuth2 인증 방식 처리
            try {
                SiteUser user = getSiteUserFromRequest(request);
                return user.getId();
            } catch (Exception e) {
                return null; // 유저가 없는 경우 null 반환
            }
        } else if (principal != null) {
            // 일반 인증 방식 처리
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                String email = authentication.getName();
                return siteUserService.findByEmail(email)
                        .map(SiteUser::getId)
                        .orElse(null); // 유저가 없는 경우 null 반환
            }
            return null;
        } else {
            return null; // Principal이 null인 경우 null 반환
        }
    }
}
