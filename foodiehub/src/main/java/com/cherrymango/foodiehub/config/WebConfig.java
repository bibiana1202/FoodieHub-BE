package com.cherrymango.foodiehub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** URL 요청을 C:/dev/foodie_hub_upload/로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/dev/foodie_hub_upload/"); // 실제 파일 경로
    }

}
