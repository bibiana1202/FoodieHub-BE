package com.cherrymango.foodiehub.config.oauth;

import com.cherrymango.foodiehub.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

// AuthorizationRequestRepository:  OAuth2 인증 과정중 인증 요청 정보를 저장,로드,삭제 하는 메서드 정의
// OAuth2 에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸수 있도록 인증 요청과 관련된 상태를 저장할 저장소
public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME="oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS =18000;

    // 요청에서 쿠키를 찾아서 OAuth2 인증요청정보를 로드
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME); // 쿠키 가져옴
        return CookieUtil.deserialize(cookie,OAuth2AuthorizationRequest.class); // 쿠키값 객체로 역직렬화
    }

    // OAuth2 인증요청 정보를 쿠키에 저장
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if(authorizationRequest == null) {
            removeAuthorizationRequestCookies(request,response); // 쿠키 삭제
            return;
        }
        CookieUtil.addCookie(response,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,CookieUtil.serialize(authorizationRequest),COOKIE_EXPIRE_SECONDS); // 쿠키 추가
    }

    // 쿠키 삭제
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME); // 쿠키 삭제
    }

    // 쿠키에 저장된 OAuth2 인증요청 정보를 제거
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

}
