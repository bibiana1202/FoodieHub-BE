package com.cherrymango.foodiehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
@Controller
public class LoginTestController {

    // 로그인 테스트 개발용 컨트롤러
    @GetMapping("/loginsuccess")
    public String loginsuccess(Model model, Principal principal) {
        String username = ""; // 기본값 설정
        // 현재 로그인한 사용자의 이름 또는 이메일 가져오기
        if (principal != null) {
            username = principal.getName(); // 일반적으로 사용자의 이름 또는 이메일
            System.out.println("Logged in user: " + username);
        } else {
            System.out.println("No user is logged in.");
        }
        model.addAttribute("username",username);

        return "loginsuccess";
    }
}
