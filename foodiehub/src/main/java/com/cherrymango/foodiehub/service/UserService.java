package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.dto.AddUserRequest;
import com.cherrymango.foodiehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Long save(AddUserRequest dto){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(SiteUser.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword1()))// 패스워드 암호화 , 패스워드를 저장할때 시큐리티를 설정하며 패스워드 인코딩용으로 등록한 빈을 사용해서 암호화후에 저장
                .nickname(dto.getNickname())
                .name(dto.getName())
                .cellphone(dto.getCellphone())
                .build()).getId();
    }
    // 전달 받은 유저 ID 로 유저를 검색해서 전달하는 메서드
    public SiteUser findById (Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
    }

    // 이메일을 입력받아 SiteUser 테이블에서 유저를 찾고, 없으면 예외 처리
    public Optional<SiteUser> findByEmail (String email){
        return userRepository.findByEmail(email);
    }

    // onAuthenticationSuccess 메서드에서 OAuth2 인증후 초기에 사용자 정보가 없을때 사용자 생성
    public SiteUser saveOAuth2User(String email, String nickname, String provider) {
        System.out.println("사용자 닉네임 nickname: " + nickname);

        SiteUser user = SiteUser.builder()
                .email(email)
                .password("") // OAuth2 사용자는 비밀번호가 필요하지 않음
                .nickname(nickname) // 이메일의 로컬 부분을 닉네임으로 사용
//                .nickname(email.split("@")[0]) // 이메일의 로컬 부분을 닉네임으로 사용
                .role("ROLE_USER") // 기본 역할 설정
                .provider(provider) // OAuth2 제공자 설정
                .build();
        return userRepository.save(user);
    }



}
