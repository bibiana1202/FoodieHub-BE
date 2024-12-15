package com.cherrymango.foodiehub.controller;

import com.cherrymango.foodiehub.dto.AddAdminRequest;
import com.cherrymango.foodiehub.dto.AddUserRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {
    @GetMapping("/login")
    public String login() {
        System.out.println("Custom login page requested"); // 디버깅 로그
        return "login";
    }

    // 회원가입 폼 요청
    @GetMapping("/signup")
    public String signupForm(Model model) {
        // AddUserRequest 객체를 모델에 추가
        if (!model.containsAttribute("addUserRequest")) {
            model.addAttribute("addUserRequest", new AddUserRequest());
        } else {
            System.out.println("모델에 이미 addUserRequest 객체가 존재합니다.");
        }
        return "signup";
    }

    // 회원가입_관리자 폼 요청
    @GetMapping("/signup_admin")
    public String signupadminForm(Model model) {
        // AddUserRequest 객체를 모델에 추가
        if (!model.containsAttribute("addAdminRequest")) {
            model.addAttribute("addAdminRequest", new AddAdminRequest());
        } else {
            System.out.println("모델에 이미 addAdminRequest 객체가 존재합니다.");
        }
        return "signup_admin";
    }
}
