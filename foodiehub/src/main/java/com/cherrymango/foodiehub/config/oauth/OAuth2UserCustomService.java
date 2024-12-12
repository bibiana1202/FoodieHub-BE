package com.cherrymango.foodiehub.config.oauth;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
// OAuth2 인증을 사용하여 소셜로그인을 처리하는 클래스 : OAuth2UserCustomService
// OAuth2 인증후 사용자 정보를 불러오는 기본 서비스 : DefaultOAuth2UserService
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    // Spring Security 에서 OAuth2 인증이 성공하면 호출되는 메서드~!!!
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 요청을 바탕으로 유저 정보를 담은 객체 반환
        // 리소스 서버에서 보내주는 사용자 정보를 불러오는 메서드
        OAuth2User oAuth2User = super.loadUser(userRequest); // OAuth2 인증요청(userRequest) 에 따라 사용자 정보 로드
        saveOrUpdate(oAuth2User); // 가져온 사용자 정보를 기반으로 데이터베이스에 사용자를 저장하거나 업데이트
        return oAuth2User;
    }

    // 유저가 있으면 업데이트, 없으면 유저 생성
    private SiteUser saveOrUpdate(OAuth2User oAuth2User) {
        Map<String,Object> attributes = oAuth2User.getAttributes(); // OAuth2 공급자로 부터 가져온 사용자 정보를 맵 형태로 추출
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        SiteUser siteUser = userRepository.findByEmail(email) // db 에서 이메일 기반으로 사용자 검색
                .map(entity->entity.update(name)) // 사용자가 이미 존재하는 경우 이름을 업데이트
                .orElse(SiteUser.builder() // 사용자가 존재하지 않을 경우 새로 생성
                        .email(email)
                        .nickname(name)
                        .build());

        return userRepository.save(siteUser);
    }

}
