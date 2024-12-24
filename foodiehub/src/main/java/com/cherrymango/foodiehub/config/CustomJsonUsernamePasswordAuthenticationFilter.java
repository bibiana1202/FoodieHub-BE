package com.cherrymango.foodiehub.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

@Log4j2
public class CustomJsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public CustomJsonUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super();
        this.objectMapper = new ObjectMapper();
        setAuthenticationManager(authenticationManager);

        // 로그인 경로 설정
        this.setFilterProcessesUrl("/api/auth/login"); // 경로와 메서드를 지정하지 않고 이 메서드로 설정
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("CustomJsonUsernamePasswordAuthenticationFilter - attemptAuthentication 호출됨");
        System.out.println("Filter(cjupaf): " + this.getClass().getSimpleName());
        System.out.println("Authentication(cjupaf): " + SecurityContextHolder.getContext().getAuthentication());

        // JSON 요청 처리
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            try {
                System.out.println("Request InputStream: " + request.getInputStream());

                // 요청 본문에서 JSON 데이터 파싱
                Map<String, String> requestBody = objectMapper.readValue(request.getInputStream(), Map.class);
                String username = requestBody.get("username");
                String password = requestBody.get("password");
                System.out.println("필터에서 읽은 username: " + username);
                System.out.println("필터에서 읽은 password: " + password);

                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
                setDetails(request, authRequest);

                // 추가된 디버깅 코드
                try {
                    Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
                    System.out.println("Authentication 성공");
                    return authentication;
                } catch (AuthenticationException e) {
                    System.out.println("Authentication 실패: " + e.getMessage());
                    throw e;
                }


//                return this.getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse authentication request body");
            }
        }
        return super.attemptAuthentication(request, response); // 기본 폼 데이터 처리
    }

//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        System.out.println("successfulAuthentication 호출됨");
//
//        // SecurityContext에 Authentication 객체 저장
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(authResult);
//
//        // 세션 정보 출력
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            System.out.println("세션 ID: " + session.getId());
//            System.out.println("세션 속성:");
//            session.getAttributeNames().asIterator().forEachRemaining(attr -> System.out.println(attr + ": " + session.getAttribute(attr)));
//        } else {
//            System.out.println("세션이 존재하지 않습니다.");
//        }
//
//        System.out.println("successfulAuthentication: " + SecurityContextHolder.getContext().getAuthentication());
//
//        Object principal = authResult.getPrincipal();
//        System.out.println("Principal after authentication: " + principal);
//
//
//        // 인증된 사용자 정보 가져오기
//        String username = authResult.getName();
//        System.out.println("인증된 사용자: " + username);
//
//        System.out.println("세션 ID: " + request.getSession(false)); // 세션이 없으면 null 반환
//        System.out.println("세션 생성 여부: " + (request.getSession(false) != null));
//
//        // 강제로 세션 생성
//        request.getSession(true); // 세션이 없으면 새로 생성
//
//        System.out.println("강제로 생성된 세션 ID: " + request.getSession().getId());
//
//
//
//        // 응답 작성
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(String.format("{\"message\": \"Authentication successful\", \"user\": \"%s\"}", username));
//    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 호출됨");

        // SecurityContext에 Authentication 객체 저장
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authResult);
        System.out.println("SecurityContext에 저장된 Authentication: " + context.getAuthentication());

        // 세션 생성 및 SecurityContext 저장
        HttpSession session = request.getSession(true); // 세션 강제 생성
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        System.out.println("SecurityContext가 세션에 저장됨: " + session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));

        // 세션 정보 디버깅
        if (session != null) {
            System.out.println("세션 ID: " + session.getId());
            System.out.println("세션 속성:");
            session.getAttributeNames().asIterator().forEachRemaining(attr -> System.out.println(attr + ": " + session.getAttribute(attr)));
        } else {
            System.out.println("세션이 존재하지 않습니다.");
        }

        // Principal 정보 출력
        Object principal = authResult.getPrincipal();
        System.out.println("Principal after authentication: " + principal);

        // 인증된 사용자 정보 가져오기
        String username = authResult.getName();
        System.out.println("인증된 사용자: " + username);

        // 사용자 권한(Role) 정보 가져오기
        String roles = authResult.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .reduce((role1, role2) -> role1 + ", " + role2)
                .orElse("ROLE_USER"); // 권한이 없을 경우 기본값 설정

        System.out.println("사용자 권한: " + roles);

        // SecurityContext 확인
        System.out.println("현재 SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());

        // 응답 작성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"message\": \"Authentication successful\", \"user\": \"%s\", \"role\": \"%s\"}", username, roles));
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("unsuccessfulAuthentication 호출됨");
        System.out.println("실패 이유: " + failed.getMessage());

        // 실패 응답 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format("{\"error\": \"Authentication failed\", \"message\": \"%s\"}", failed.getMessage());
        response.getWriter().write(jsonResponse);

        // 부모 클래스의 기본 동작 호출 (선택 사항)
        super.unsuccessfulAuthentication(request, response, failed);
    }

}
