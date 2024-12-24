package com.cherrymango.foodiehub.controller;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final UserService userService;

    @GetMapping("/api/mypage/profileinfo")
    public Map<String, Object> getUserInfo(Principal principal) {
        String email ="";
        String nickname ="";
        Map<String, Object> response = new HashMap<>();

        // OAuth2 로그인 처리
        if (principal != null && principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) principal;

            // 사용자 속성 가져오기
            Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
            System.out.println("oauth2 권한: " + attributes);
            email = (String) attributes.get("email");
            System.out.println("이메일: "+email);
        } else {
            // 폼 로그인 처리
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("권한: " + authentication);

            if (authentication != null) {
                email = authentication.getName();//authentication.getName(); // 사용자 이름 가져오기
                System.out.println("이메일: "+email);

            }
        }


        // 사용자 확인 또는 생성
        if (email == null || email.isEmpty()) {
            response.put("error", "Email is missing");
            return response;
        }
        Optional<SiteUser> siteUser = userService.findByEmail(email);
        if (siteUser.isPresent()) {
            SiteUser user = siteUser.get();
            System.out.println("사용자 이메일: " + user.getEmail());
            System.out.println("사용자 닉네임: " + user.getNickname());
            nickname = user.getNickname();
        }



        response.put("nickname", nickname);
        response.put("email", email);
        return response;
    }



}
