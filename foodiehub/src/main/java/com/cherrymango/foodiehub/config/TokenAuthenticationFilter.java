package com.cherrymango.foodiehub.config;

import com.cherrymango.foodiehub.config.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    // JWT 토큰을 이용한 인증 필터를 구현하는 클래스
    // Spring Security 와 함께 사용되어, 요청이 올때 마다 사용자의 토큰을 검사하고 인증정보를 설정해준다.
    // 모든 요청마다 한번씩 실행되는 필터
    // JWT 토큰을 이용하여 인증된 사용자 정보를 설정하고, 이후의 요청이 인증된 상태로 처리될 수 있도록 합니다.
    private final TokenProvider tokenProvider; // 서비스
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Filter(taf): " + this.getClass().getSimpleName());
        System.out.println("Authentication(taf): " + SecurityContextHolder.getContext().getAuthentication());

        // 현재 Authentication이 OAuth2AuthenticationToken일 경우 덮어쓰지 않음
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth instanceof OAuth2AuthenticationToken && currentAuth.isAuthenticated()) {
            System.out.println("OAuth2AuthenticationToken detected, skipping TokenAuthenticationFilter.");
            filterChain.doFilter(request, response);
            return;
        }

        // 필터의 주요 로직을 처리
        // HTTP 요청이 들어올 때 마다 호출되며, 토큰을 검사하고, 유효한 경우 인증정보를 설정합니다.

        // 요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        // 가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);
        // 가져온 토큰이 유효한지 확인하고, 유효한 때는 인증 정보 설정
        if (tokenProvider.validToken(token)) { // 토큰의 유효성 검사
            Authentication authentication = tokenProvider.getAuthentication(token); // 사용자의 인증정보 가져옵니다.
            SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 정보를 SecurityContext에 설정
        }
        // 이 과정이 완료되면, 이후의 요청은 인증된 사용자가 된 것 입니다.
        System.out.println("After Authentication(taf): " + SecurityContextHolder.getContext().getAuthentication());

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);// 인증 필터링이 끝난후, 요청을 다음 필터로 전달합니다. 이를 통해 정상적인 응답처리가 가능해 집니다.
        // 필터 체인 내에서 다음 필터를 호출하는 메서드로서, 이를 통해 연속적으로 필터들이 순차적으로 요청을 처리할 수 있도록 해줍니다. 이를 호출하지 않으면 필터 체인의 처리가 중단되고, 요청이 최종 서블릿까지 도달하지 못할 수 있습니다.

    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
