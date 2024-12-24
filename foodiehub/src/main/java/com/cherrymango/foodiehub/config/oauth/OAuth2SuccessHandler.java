package com.cherrymango.foodiehub.config.oauth;

import com.cherrymango.foodiehub.config.jwt.TokenProvider;
import com.cherrymango.foodiehub.domain.RefreshToken;
import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.repository.RefreshTokenRepository;
import com.cherrymango.foodiehub.service.UserService;
import com.cherrymango.foodiehub.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@Component
// OAuth2 인증 성공후 수행되는 작업 정의
// 스프링 시큐리티 기본 로직에서는 별도의 authenticationSuccessHandler 를 지정하지 않으면 로그인 성공 이후 SimpleUrlAuthenticationSuccessHandler 를 사용합니다.
// 일반적인 로직은 동일하고, 토큰과 관련된 작업만 추가로 처리하기 위해 상속받은뒤에 오버라이드한것.
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/main";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    // OAuth2 인증이 성공한 후 호출되는 메서드!!
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {


        System.out.println("석세스핸들러!!!!!!!!!!!!!!!");
        if (authentication instanceof OAuth2AuthenticationToken) {
            System.out.println("OAuth2AuthenticationToken detected!");
        } else {
            System.out.println("Unexpected Authentication type: " + authentication.getClass().getName());
        }

        System.out.println("SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());


        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // 사용자 정보 로드

        // 사용자 정보 처리
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println("attributres:" + attributes);

        // OAuth2UserRequest에서 registrationId 추출
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        System.out.println("registrationId: " + registrationId);


        String email;
        String nickname; // 카카오와 구글 모두 닉네임 처리

        // 소셜 로그인 제공자별 사용자 정보 추출
        switch (registrationId) {
            case "google":
                email = (String) attributes.get("email");
                nickname = (String) attributes.get("name");
                break;

            case "kakao":
                // 카카오 계정의 사용자 정보 추출
                email = (String) oAuth2User.getAttributes().get("email");
                nickname = (String) oAuth2User.getAttributes().get("nickname");
                break;

            case "naver":
                // 네이버는 attributes에 바로 데이터가 포함되어 있음
                email = (String) attributes.get("email");
                nickname = (String) attributes.get("nickname");
                break;

            default:
                throw new IllegalArgumentException("지원하지 않는 소셜 로그인 제공자: " + registrationId);
        }

        if (email == null) {
            throw new IllegalArgumentException(registrationId + " 계정에 이메일이 없습니다.");
        }

        // 닉네임에 소셜 제공자 이름 추가
        if (nickname == null) {
            nickname = "Anonymous"; // 닉네임이 없을 경우 기본값
        }
        System.out.println("사용자 닉네임 nickname: " + nickname);
        String customizedNickname = nickname + "@" + registrationId;
        System.out.println("사용자 닉네임: " + customizedNickname);
        System.out.println("사용자 닉네임 email: " + email);

        // 2. 사용자 확인 또는 생성
        SiteUser siteUser = userService.findByEmail(email)
                .orElseGet(() -> userService.saveOAuth2User(email, customizedNickname, registrationId));
        System.out.println("사용자 닉네임 siteUser: " + siteUser.getEmail());

        // 1. 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(siteUser, REFRESH_TOKEN_DURATION);
        saveRefreshToken(siteUser.getId(), refreshToken); // 데이터베이스에 저장
        addRefreshTokenToCookie(request, response, refreshToken); // 쿠키에 저장, 클라이언트에서 액세스 토큰이 만료되면 재발급 요청하도록 쿠키에 리프레시 토큰을 저장합니다
        // 2. 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(siteUser, ACCESS_TOKEN_DURATION);

        // SecurityContext에 인증 정보 유지
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 리다이렉트 처리
        String targetUrl = getTargetUrl(accessToken);
        // 3. 인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response); // OAuth2 인증 중 사용된 임시 데이터를 초기화 합니다. 예를 들어 세션에 저장된 인증 요청 정보 등을 제거

        // OAuth2 인증 성공 시 세션 초기화
//        SecurityContextHolder.clearContext();

        SecurityContext context = SecurityContextHolder.getContext();
        System.out.println("석세스 핸들러, Authentication in SecurityContext: " + context.getAuthentication());


        // 2번에서 만든 url로 리다이렉트합니다. (클라이언트로 리다이렉트)
        getRedirectStrategy().sendRedirect(request, response, targetUrl); // 클라이언트를 targeturl로 리다이렉트 합니다. 이 url에는 액세스 토큰이 포함될수 있습니다.

    }

    // 생성된 리프레시 토큰을 전달받아 데이터베이스에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken)) // 있으면 업데이트
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // 생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // 인증 관련 설정값 ,쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    // 액세스 토큰을 패스에 추가
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build().toUriString();
    }

}
