package com.cherrymango.foodiehub.config.jwt;

import com.cherrymango.foodiehub.domain.SiteUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
// 토큰을 생성하고 올바른 토큰인지 유효성 검사를 하고, 토큰에서 필요한 정보를 가져오는 클래스
public class TokenProvider {

    private final JwtProperties jwtProperties;

    // 1. JWT 토큰 생성 메서드
    public String generateToken(SiteUser siteUser, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), siteUser);

    }

    public String makeToken(Date expiry, SiteUser siteUser) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : JWT
                .setIssuer(jwtProperties.getIssuer()) // 내용 iss(발급자) : wew12025@gmail.com(properties 에서 설정한 값)
                .setIssuedAt(now) // 내용 iat(발급일시) : 현재 시간
                .setExpiration(expiry) // 내용 exp(만료 일시) : expiry 멤버 변숫값
                .setSubject(siteUser.getEmail()) // 내용 sub(토큰 제목) : 유저의 이메일

                .claim("id", siteUser.getId()) // 클레임 id : 유저 ID
                .claim("role", siteUser.getRole()) // 사용자 역할 추가

                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명 : 비밀값과 함께 해시값을 HS256 방식으로 암호화
                .compact(); // JWT(Java Web Token) 의 최종 문자열 형태로의 인코딩 수행(JWT 토큰 문자열)

    }

    // 2.JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화, 복호화 과정에서 에러가 발생하면 유효하지 않은 토큰
                    .parseClaimsJws(token); // 주어진 토큰을 파싱하고 서명을 검증함.
            return true; // 예외가 발생하지 않으면 유효한 토큰
        } catch (Exception e) {
            return false;
        }
    }

    //3. 토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token); // 토큰에서 클레임 정보 추출
        String role = claims.get("role", String.class); // 역할 정보 추출

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(role));
        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
    }

    private Claims getClaims(String token) {
        return Jwts.parser() // 클레임 조회
                .setSigningKey(jwtProperties.getSecretKey())// 비밀 키를 이용해 토큰을 복호화
                .parseClaimsJws(token) // 토큰 파싱 및 서명 검증
                .getBody(); // 클레임(Claims) 정보를 반환
    }

    // 4. 토큰 기반으로 유저 ID 를 가져오는 메서드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }
}
