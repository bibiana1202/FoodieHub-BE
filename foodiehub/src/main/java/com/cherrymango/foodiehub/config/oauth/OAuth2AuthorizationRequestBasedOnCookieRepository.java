package com.cherrymango.foodiehub.config.oauth;

import com.cherrymango.foodiehub.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

@Log4j2
// AuthorizationRequestRepository:  OAuth2 인증 과정중 인증 요청 정보를 저장,로드,삭제 하는 메서드 정의
// OAuth2 에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸수 있도록 인증 요청과 관련된 상태를 저장할 저장소
public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME="oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS =18000;

    // 디버깅용 생성자
    public OAuth2AuthorizationRequestBasedOnCookieRepository() {
        System.out.println("OAuth2AuthorizationRequestBasedOnCookieRepository initialized");
    }

    // 요청에서 쿠키를 찾아서 OAuth2 인증요청정보를 로드
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        System.out.println("loadAuthorizationRequest called");

        //        Cookie cookie = WebUtils.getCookie(request,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME); // 쿠키 가져옴
//        return CookieUtil.deserialize(cookie,OAuth2AuthorizationRequest.class); // 쿠키값 객체로 역직렬화
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        if (cookie == null) {
            log.debug("No OAuth2 Authorization Request Cookie found.");
            return null;
        }

        // 디버깅 로그 추가
        log.debug("Found OAuth2 Authorization Request Cookie: " + cookie.getValue());

        OAuth2AuthorizationRequest authorizationRequest = CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
        if (authorizationRequest != null) {
            log.debug("Loaded Authorization Request: " + authorizationRequest);
            log.debug("Loaded state parameter: " + authorizationRequest.getState());
        } else {
            log.debug("Failed to deserialize Authorization Request from Cookie.");
        }

        return authorizationRequest;

    }

    // OAuth2 인증요청 정보를 쿠키에 저장
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("saveAuthorizationRequest called");
        log.debug("Saving state: " + authorizationRequest.getState());

        if(authorizationRequest == null) {
            log.debug("Authorization Request is null. Removing cookies.");
            removeAuthorizationRequestCookies(request,response); // 쿠키 삭제
            return;
        }

        // 디버깅 로그 추가
        log.debug("Saving Authorization Request: " + authorizationRequest);
        log.debug("Saving state parameter: " + authorizationRequest.getState());

        // 쿠키 저장
        log.debug("Saving Authorization Request to Cookie: " + authorizationRequest);
        CookieUtil.addCookie(response,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,CookieUtil.serialize(authorizationRequest),COOKIE_EXPIRE_SECONDS); // 쿠키 추가
    }

    // 쿠키 삭제
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("removeAuthorizationRequest called");

        log.debug("Removing OAuth2 Authorization Request Cookie.");
        CookieUtil.deleteCookie(request,response,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME); // 쿠키 삭제
    }

    // 쿠키에 저장된 OAuth2 인증요청 정보를 제거
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
//        return this.loadAuthorizationRequest(request);
        // 쿠키에서 인증 요청 정보를 로드
        OAuth2AuthorizationRequest authorizationRequest = this.loadAuthorizationRequest(request);
        log.debug("Removing state: " + authorizationRequest.getState());

        // 쿠키를 삭제
        this.removeAuthorizationRequestCookies(request, response);

        // 인증 요청 정보 반환
        return authorizationRequest;
    }

}
