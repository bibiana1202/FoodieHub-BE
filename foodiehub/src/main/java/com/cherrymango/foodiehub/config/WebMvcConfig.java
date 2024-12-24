package com.cherrymango.foodiehub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{spring:\\w+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{spring:\\w+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/")
                .setViewName("forward:/index.html");

//        // 제외 설정 추가
//        registry.addViewController("/api/**").setViewName("forward:/api"); // API 제외
//        registry.addViewController("/css/**").setViewName("forward:/css"); // 정적 리소스 제외
    }

}
