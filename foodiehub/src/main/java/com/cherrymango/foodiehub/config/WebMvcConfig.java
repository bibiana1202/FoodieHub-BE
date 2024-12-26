//package com.cherrymango.foodiehub.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///*
// * 어떤 경로로 접근하더라도 클라이언트(SPA)에서 라우팅을 담당하도록 하기 위해,
// * 요청을 모두 index.html로 포워드해주는 설정
// */
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        // {spring:\\w+} -> 한 단어짜리 경로 (ex: /login, /board, /user 등)
//        registry.addViewController("/{spring:\\w+}")
//                .setViewName("forward:/index.html");
//
//        // /**/{spring:\\w+} -> 여러 경로 뒤에 한 단어가 붙는 경우 (ex: /board/123, /user/edit 등)
//        registry.addViewController("/**/{spring:\\w+}")
//                .setViewName("forward:/index.html");
//
//        // 루트 경로
//        registry.addViewController("/")
//                .setViewName("forward:/index.html");
//
//    }
//
//}

