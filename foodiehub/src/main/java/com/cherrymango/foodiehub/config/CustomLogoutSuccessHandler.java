package com.cherrymango.foodiehub.config;

import com.cherrymango.foodiehub.repository.RefreshTokenRepository;
import com.cherrymango.foodiehub.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
            try {
                refreshTokenService.deleteByToken(refreshToken);
                System.out.println("Refresh token deleted: " + refreshToken);
            } catch (Exception e) {
                System.err.println("Failed to delete refresh token: " + e.getMessage());
            }
        }

        // 쿠키 무효화
//        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setMaxAge(0); // 즉시 만료
//        response.addCookie(refreshTokenCookie);
        invalidateCookie(response, "refresh_token");
        invalidateCookie(response, "JSESSIONID");


        // 세션 무효화 및 리다이렉트
//        request.getSession().invalidate();
//        System.out.println("Session invalidated.");
        HttpSession session = request.getSession(false); // false: 세션이 없으면 생성하지 않음
        if (session != null) {
            session.invalidate();
            System.out.println("Session invalidated.");
        }

//        response.sendRedirect("/login");
//        // 상태 코드 반환 (리다이렉트 없이)
//        response.setStatus(HttpServletResponse.SC_OK); // 200 상태 코드만 반환
//        response.getWriter().write("Logout successful");
//        response.getWriter().flush();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Logout successful\"}");
        response.getWriter().flush();

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

    private void invalidateCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);
    }
}
