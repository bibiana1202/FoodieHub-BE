package com.cherrymango.foodiehub.util;

import com.cherrymango.foodiehub.config.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private Key getSigningKey() {
        System.out.println("SECRET_KEY used: " + jwtProperties.getSecretKey());
        System.out.println("SECRET_KEY length: " + jwtProperties.getSecretKey().length());
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    public Claims extractClaims(String token) {
        // Keys.hmacShaKeyFor()를 사용해 Key 객체 생성
        Key signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(signingKey) // 동일한 Key 객체 사용
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        System.out.println(token);
        return extractClaims(token).getSubject(); // sub(토큰 제목)에서 이메일 추출
    }

    public Long extractUserId(String token) {
        return extractClaims(token).get("id", Long.class); // 클레임 id에서 유저 ID 추출
    }

    public String extractUserRole(String token) {
        return extractClaims(token).get("role", String.class); // 클레임 role에서 역할(Role) 추출
    }

}
