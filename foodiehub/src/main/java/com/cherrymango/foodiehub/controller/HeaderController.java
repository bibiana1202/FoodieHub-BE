package com.cherrymango.foodiehub.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {
    // 로그인 성공 시 사용자 정보 반환
    @GetMapping("/api/user/main")
    public Map<String, Object> loginsuccess(HttpServletRequest request,Principal principal) {// 세션 정보 출력
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("ls세션 ID: " + session.getId());
            System.out.println("ls세션 속성:");
            session.getAttributeNames().asIterator().forEachRemaining(attr -> System.out.println(attr + ": " + session.getAttribute(attr)));
        } else {
            System.out.println("ls세션이 존재하지 않습니다.");
        }


        Map<String, Object> response = new HashMap<>();
        System.out.println("principal 권한: " + principal);

        String username = "";
        String username_google = "";
        String username_kakao = "";
        String email = "";
        String role = "";

        if (principal != null) {
            // OAuth2 로그인 처리
            if (principal instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) principal;

                // 사용자 속성 가져오기
                Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
                System.out.println("oauth2 권한: " + attributes);

                username_google = (String) attributes.get("name"); // Google 로그인 이름
                username_kakao = (String) attributes.get("nickname"); // Kakao 로그인 이름
                email = (String) attributes.get("email");

                // 권한 정보 가져오기
                role = authToken.getAuthorities().stream()
                        .findFirst()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .orElse("NO_ROLE");

                // 이름 설정
                username = (username_kakao != null) ? username_kakao : username_google;

            } else {
                // 폼 로그인 처리
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("권한: " + authentication);

                if (authentication != null) {
                    username = authentication.getName();//authentication.getName(); // 사용자 이름 가져오기

                    // 권한 정보 설정 (첫 번째 권한만 가져오기)
                    role = authentication.getAuthorities().stream()
                            .findFirst()
                            .map(grantedAuthority -> grantedAuthority.getAuthority())
                            .orElse("ROLE_USER");
                }
            }
        }

        // JSON 응답 데이터 구성
        response.put("username", username);
        response.put("email", email);
        response.put("role", role);

        return response;
    }
}
