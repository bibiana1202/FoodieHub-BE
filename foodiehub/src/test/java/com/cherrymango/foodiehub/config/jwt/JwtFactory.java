package com.cherrymango.foodiehub.config.jwt;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Getter
public class JwtFactory {
    private String subject = "test@email.com";
    private Date issuedAt = new Date();
    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
    private Map<String, Object> claims = new HashMap<>();

    // 빌더 패턴을 사용해 설정이 필요한 데이터만 선택 설정
    @Builder
    public JwtFactory(String subject, Date issuedAt, Date expiration, Map<String,Object> claims){
        this.subject = subject != null ? subject : this.subject;
        this.issuedAt = issuedAt != null ? issuedAt : this.issuedAt;
        this.expiration = expiration != null ? expiration : this.expiration;
//        this.claims = claims != null ? claims : this.claims;
        // 클레임 병합: 기존 클레임에 새로운 클레임 추가
//        if (claims != null && !claims.isEmpty()) {
//            this.claims.putAll(claims);
//        }
        this.claims = claims != null ? new HashMap<>(claims) : new HashMap<>(); // 변경 가능하게 초기화

    }

    public JwtFactory addClaim(String key, Object value) {
        this.claims.put(key, value);
        return this;
    }

    // 기본 빌더를 사용해서 JwtFactory 객체를 생성하는 것일 뿐, 특정한 기본 값을 설정하는 부분은 없습니다.
    // 만약 기본적인 설정을 추가하지 않으면, 생성된 객체는 단순히 초기 상태의 빈 객체가 됩니다.
    public static JwtFactory withDefaultValues(){
        return JwtFactory.builder().build();
    }

    // jjwt 라이브러리를 사용해 JWT 토큰 생성
    public String createToken(JwtProperties jwtProperties){
        return Jwts.builder()
                .setSubject(subject)
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256,jwtProperties.getSecretKey())
                .compact();
    }


}

