package com.cherrymango.foodiehub.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component // Spring 컨테이너 에서 관리하는 Bean으로 등록
@ConfigurationProperties("jwt") // 자바 클래스에 프로피티값을 가져와서 사용하는 애너테이션
// jwt 값을 변수로 접근하는데 사용
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
