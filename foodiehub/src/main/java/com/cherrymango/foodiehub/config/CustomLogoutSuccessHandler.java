package com.cherrymango.foodiehub.config;

import com.cherrymango.foodiehub.repository.RefreshTokenRepository;
import com.cherrymango.foodiehub.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final RefreshTokenService refreshTokenService; // 서비스 주입

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("CustomLogoutSuccessHandler invoked.");

        // 리프레시 토큰 삭제
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            refreshTokenService.deleteByToken(refreshToken);
            System.out.println("Refresh token deleted: " + refreshToken);
        }

        // 쿠키 무효화
        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(refreshTokenCookie);


        // 세션 무효화 및 리다이렉트
        request.getSession().invalidate();
        System.out.println("Session invalidated.");

        response.sendRedirect("/login");

    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
