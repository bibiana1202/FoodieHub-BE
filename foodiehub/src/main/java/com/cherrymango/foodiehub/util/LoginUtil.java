package com.cherrymango.foodiehub.util;

import com.cherrymango.foodiehub.domain.Role;
import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.service.SiteUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginUtil {

    private final SiteUserService siteUserService;
    private final TokenUtil tokenUtil;

    public SiteUser getSiteUserFromLogin(HttpServletRequest request, Principal principal) {
        // OAuth2 로그인 처리
        if(principal!= null && principal instanceof OAuth2AuthenticationToken){
            // 사용자 정보를 TokenUtil에서 가져오기
            SiteUser siteUser = tokenUtil.getSiteUserFromRequest(request);

            return siteUser;
        }
        // 폼 로그인 처리
        else if(principal!= null){
            String email="";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                email = authentication.getName();//authentication.getName(); // 사용자 이름 가져오기
            }
            // 사용자 데이터 조회
            Optional<SiteUser> optionalSiteUser = siteUserService.findByEmail(email);
            SiteUser siteUser = optionalSiteUser.orElseThrow(() -> new RuntimeException("User not found"));
            return siteUser;
        }
        else{
            return null;
        }

    }
}
