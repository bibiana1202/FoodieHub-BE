package com.cherrymango.foodiehub.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Map;

@Controller
public class LoginTestController {

    // 로그인 테스트 개발용 컨트롤러
    @GetMapping("/loginsuccess")
    public String loginsuccess(Model model, Principal principal) {
//        String username = ""; // 기본값 설정
//        // 현재 로그인한 사용자의 이름 또는 이메일 가져오기
//        if (principal != null) {
//            username = principal.getName(); // 일반적으로 사용자의 이름 또는 이메일
//
//            System.out.println("principal: " + principal);
//            System.out.println("Logged in user: " + username);
//        } else {
//            System.out.println("No user is logged in.");
//        }
//        model.addAttribute("username",username);
//
//        return "loginsuccess";
        String username = ""; // 기본값 설정
        String username_google = ""; // 기본값 설정
        String username_kakao = ""; // 기본값 설정
        String email = ""; // 기본값 설정
        String authorities = ""; // 권한 정보 저장
        String role = ""; // 사용자 권한


        if (principal instanceof OAuth2AuthenticationToken) { // principal이 OAuth2AuthenticationToken인지 확인
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) principal;
            // User Attributes에서 name 가져오기
            Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
            username_google = (String) attributes.get("name"); // 'name' 속성에서 이름 가져오기
            username_kakao = (String) attributes.get("nickname"); // 'nickname' 속성에서 이름 가져오기
            email = (String) attributes.get("email");
            // 권한 정보 가져오기
            authorities = authToken.getAuthorities().toString();
            System.out.println("User authorities: " + authorities);
            // 권한 가져오기 (첫 번째 권한만 표시)
            role = authToken.getAuthorities().stream()
                    .findFirst() // 첫 번째 권한만 가져옴
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .orElse("NO_ROLE"); // 권한이 없으면 기본값 설정


            System.out.println("principal: " + principal);
            System.out.println("Logged in user_google: " + username_google);
            System.out.println("Logged in username_kakao: " + username_kakao);
            System.out.println("Logged in useremail: " + email);

        } else {
            System.out.println("No user is logged in.");
        }

        if(username_kakao == null || username_google !=null){
            username = username_google;
        }
        else if(username_kakao !=null || username_google ==null) {
            username= username_kakao;
        }
        model.addAttribute("username", username);
        model.addAttribute("authorities", authorities);
        model.addAttribute("role", role);

        return "loginsuccess";
    }
}
