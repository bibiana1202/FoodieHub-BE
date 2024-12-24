package com.cherrymango.foodiehub.config;

import com.cherrymango.foodiehub.config.jwt.TokenProvider;
import com.cherrymango.foodiehub.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.cherrymango.foodiehub.config.oauth.OAuth2SuccessHandler;
import com.cherrymango.foodiehub.config.oauth.OAuth2UserCustomService;
import com.cherrymango.foodiehub.repository.RefreshTokenRepository;
import com.cherrymango.foodiehub.service.UserDetailService;
import com.cherrymango.foodiehub.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration // 스프링의 환경 설정 파일
//@EnableWebSecurity // 모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 애너테이션 , 스프링 시큐리티를 활성화 하는 역할
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;


    // 스프링 시큐리티 기능 비활성화 : 스프링 시큐리티의 모든 기능을 사용하지 않게 설정하는 코드.
    // 즉, 인증,인가 서비스를 모든곳에 적용하지 않습니다.
    // 일반적으로 정적 리소스(이미지,HTML 파일)에 설정. 정적 리소스만 스프링 시큐리티 사용을 비활성화 하는데
    // static 하위 경로에 있는 리소스와 h2의 데이터를 확인하는데 사용하는 h2-console 하위 url 을 대상으로 ignoring() 메서드를 사용
    @Bean
    public WebSecurityCustomizer configure() { // 스프링 시큐리티 기능 비활성화
        return (web) -> web.ignoring()
                .requestMatchers(
                        new AntPathRequestMatcher("/img/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**")
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        return http
                // CORS 설정 추가
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 1. 토큰 방식으로 인증을 하기 때문에 기본에 사용하던 폼 로그인 ,세션 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login") // 사용자 정의 로그인 페이지 경로
//                        .loginProcessingUrl("/api/auth/login") // 로그인 요청 처리 URL
//                        .defaultSuccessUrl("/main", true) // 로그인 성공 후 이동할 경로
//                        .failureUrl("/login?error=true") // 로그인 실패 시 이동할 경로
//                        .permitAll() // 로그인 관련 페이지는 모두 접근 가능
//                )
//                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 필요 시에만 세션 생성
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 필요 시 세션 생성
                        .sessionFixation().newSession() // 로그인/재인증 시 새로운 세션 ID 발급
                )
//                // **커스텀 필터 등록**
//                .addFilterBefore(customJsonUsernamePasswordAuthenticationFilter(authManager), UsernamePasswordAuthenticationFilter.class)
//
//                // 2. 헤더를 확인할 커스텀 필터 추가
////                .addFilterAfter(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .addFilterAfter(tokenAuthenticationFilter(), OAuth2LoginAuthenticationFilter.class)

                // 3. 토큰 재발급 URL 은 인증 없이 접근 가능하도록 설정. 나머지 API URL 은 인증 필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/favicon.ico",
                                "/logo192.png",
                                "/logo512.png",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll() // 정적 리소스는 인증 없이 접근 가능

                        .requestMatchers(new AntPathRequestMatcher("/api/token")).permitAll()  // 권한 필요 없음
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/login")).permitAll()  // 권한 필요 없음
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/logout")).permitAll()  // 권한 필요 없음
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/user")).permitAll()  // 권한 필요 없음
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/admin")).permitAll()  // 권한 필요 없음
                        .requestMatchers(new AntPathRequestMatcher("/api/user/check-nickname")).permitAll()  // 권한 필요 없음
                        .requestMatchers(new AntPathRequestMatcher("/api/header/user")).permitAll()  // 권한 필요 없음
                        .requestMatchers("/api/business/status").permitAll()  // 해당 엔드포인트 접근 허용
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated() // 권한 필요
                        .anyRequest().permitAll()) // 나머지 권한 필요 없음


                // Spring의 OAuth2 로그인을 설정, OAuth2 클라이언트를 통해 사용자 인증을 처리
                .oauth2Login(oauth2 -> oauth2
                        // 로그인 페이지 URL 설정
                        .loginPage("/login")
                        // 4. Authorization 요청과 관련된 상태 저장 , OAuth2에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸수 있도록 인증요청 관련된 상태를 저장할 저장소
                        .authorizationEndpoint(authorizationEndpoint -> {
                            System.out.println("Registering custom AuthorizationRequestRepository...");
                            authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository());
                        })
                        // 사용자 정보 엔드포인트 설정 : 사용자 정보를 처리하는 서비스 설정, 이단계에서 사용자정보를 db에 연동하거나 추가 처리 할수 있다.
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
                        // 5. 인증 성공 시 실행할 핸들러 , 사용자가 인증후 특정 url로 리다이렉트하거나, jwt 토큰을 발급하는등의 작업을 수행
                        .successHandler(oAuth2SuccessHandler())
                        .failureHandler((request, response, exception) -> {
                            System.err.println("OAuth2 login failed. Error: " + exception.getMessage());
                            exception.printStackTrace();
                            response.sendRedirect("/login?error=" + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8));

                        })
                )
                // **커스텀 필터 등록**
                .addFilterBefore(customJsonUsernamePasswordAuthenticationFilter(authManager), UsernamePasswordAuthenticationFilter.class)

                // 2. 헤더를 확인할 커스텀 필터 추가
                .addFilterAfter(tokenAuthenticationFilter(), OAuth2LoginAuthenticationFilter.class)

                // 6. /api 로 시작하는 url 인 경우 401 상태 코드를 반환하도록 예외 처리
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        ))

                // 로그아웃 설정 (컨트롤러에서 처리하도록 설정)
                //이 설정은 /logout을 직접 호출하지 않는 한, 자바스크립트 로그아웃 로직과 충돌하지 않습니다. 하지만 /logout을 호출하면 여전히 Spring Security의 기본 로그아웃 처리가 수행됩니다.
                .logout(logout -> logout
                                .logoutUrl("/api/auth/logout") // 로그아웃 URL 설정
//                        .logoutSuccessUrl("/login") // 로그아웃 성공 시 리다이렉트할 URL
                                .logoutSuccessHandler(customLogoutSuccessHandler) // 커스텀 핸들러 적용
                                .invalidateHttpSession(true) // 세션 무효화
                                .deleteCookies("JSESSIONID", "OAUTH2_AUTHORIZATION_REQUEST") // OAuth2 관련 쿠키도 삭제
                                .permitAll() // 로그아웃 경로에 대한 접근 허용
                )
//                .sessionManagement(session -> session
//                        .sessionFixation().newSession() // 로그인/재인증 시 새로운 세션 ID 발급
//                )

                .build();
    }


    // 인증 관리자 관련 설정. 사용자 정보를 가져올 서비스를 재정의 하거나, 인증방법, 예를들어 LDAP,JDBC 기반 인증 등을 설정할때 사용
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService); // 사용자 정보 서비스 설정
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);

    }

    // OAuth2 인증 성공후 수행되는 작업 정의
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository, oAuth2AuthorizationRequestBasedOnCookieRepository(), userService);
    }

    // JWT 토큰을 이용한 인증 필터를 구현하는 클래스
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    // OAuth2 에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸수 있도록 인증 요청과 관련된 상태를 저장할 저장소
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        System.out.println("OAuth2AuthorizationRequestBasedOnCookieRepository initialized");
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    // 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter(AuthenticationManager authManager) {
        return new CustomJsonUsernamePasswordAuthenticationFilter(authManager); // AuthenticationManager 전달
    }


    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 쿠키 허용
        config.addAllowedOrigin("http://localhost:8080"); // 허용할 도메인
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 설정 적용
        return source;
    }



}

