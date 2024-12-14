package com.cherrymango.foodiehub.config.oauth;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
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
        System.out.println("로드유저!!!!!!!!!!!!!!!!!!");
        // Access Token 확인
        String accessToken = userRequest.getAccessToken().getTokenValue();
        System.out.println("Access Token: " + accessToken);

        // 사용자 정보 요청
        OAuth2User user = super.loadUser(userRequest);
        System.out.println("User Attributes: " + user.getAttributes());


        // 요청을 바탕으로 유저 정보를 담은 객체 반환
        // 리소스 서버에서 보내주는 사용자 정보를 불러오는 메서드
        OAuth2User oAuth2User = super.loadUser(userRequest); // OAuth2 인증요청(userRequest) 에 따라 사용자 정보 로드
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 구글, 카카오 식별자
        SiteUser siteUser;

        // 카카오는 Access Token만 제공되므로 사용자 정보를 가져오기 위해 loadUser 메서드가 반드시 호출됩니다.
        if ("kakao".equals(registrationId)) {
            siteUser = saveOrUpdateForKakao(oAuth2User);
        // 구글은 ID Token에 사용자 정보를 포함하므로 loadUser가 호출되지 않을 수 있습니다.
        } else {
            siteUser = saveOrUpdateForGoogle(oAuth2User);
        }

        return createCustomOAuth2User(siteUser, oAuth2User);


//        saveOrUpdate(oAuth2User); // 가져온 사용자 정보를 기반으로 데이터베이스에 사용자를 저장하거나 업데이트
//        return oAuth2User;
    }

    // 유저가 있으면 업데이트, 없으면 유저 생성
//    private SiteUser saveOrUpdate(OAuth2User oAuth2User) {
//        Map<String,Object> attributes = oAuth2User.getAttributes(); // OAuth2 공급자로 부터 가져온 사용자 정보를 맵 형태로 추출
//
//        String email = (String) attributes.get("email");
//        String name = (String) attributes.get("name");
//
//        SiteUser siteUser = userRepository.findByEmail(email) // db 에서 이메일 기반으로 사용자 검색
//                .map(entity->entity.update(name)) // 사용자가 이미 존재하는 경우 이름을 업데이트
//                .orElse(SiteUser.builder() // 사용자가 존재하지 않을 경우 새로 생성
//                        .email(email)
//                        .nickname(name)
//                        .build());
//
//        return userRepository.save(siteUser);
//    }

    private SiteUser saveOrUpdateForGoogle(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        String customizedNickname = name + "@google";

        return userRepository.findByEmail(email)
                .map(user -> user.update(name))
                .orElseGet(() -> userRepository.save(
                        SiteUser.builder()
                                .email(email)
                                .nickname(customizedNickname)
                                .provider("google")
                                .build()
                ));
    }

    private SiteUser saveOrUpdateForKakao(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String name = (String) properties.get("nickname");
        String id = attributes.get("id").toString();
        String nickname = (String) profile.get("nickname");


        // 추가적인 디버깅 출력 (필요 시)
        System.out.println("attributes: " + attributes);
        System.out.println("Kakao ID: " + id);
        System.out.println("Nickname: " + nickname);
        System.out.println("name: " + name);
        System.out.println("Email: " + email);

        if (email == null) {
            throw new IllegalArgumentException("카카오 계정에 이메일이 없습니다.");
        }

        String customizedNickname = nickname + "@kakao";

        return userRepository.findByEmail(email)
                .map(user -> user.update(name))
                .orElseGet(() -> userRepository.save(
                        SiteUser.builder()
                                .email(email)
                                .nickname(customizedNickname)
                                .provider("kakao")
                                .build()
                ));
    }

    private OAuth2User createCustomOAuth2User(SiteUser siteUser, OAuth2User oAuth2User) {
        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                Map.of(
                        "email", siteUser.getEmail(),
                        "nickname", siteUser.getNickname()
                ),
                "email"
        );
    }

}
