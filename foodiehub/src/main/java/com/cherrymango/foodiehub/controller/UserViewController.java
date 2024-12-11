package com.cherrymango.foodiehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {
    @GetMapping("/login")
    public String login() {
        System.out.println("Custom login page requested"); // 디버깅 로그
        return "login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }
}
