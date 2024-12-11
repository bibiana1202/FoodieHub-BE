package com.cherrymango.foodiehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String root() {
        System.out.println("hello");
        return "home";
    }
}
