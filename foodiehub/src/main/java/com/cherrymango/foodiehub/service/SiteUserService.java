package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.dto.AddAdminRequest;
import com.cherrymango.foodiehub.dto.AddUserRequest;
import com.cherrymango.foodiehub.dto.SiteUserProfileRequest;
import com.cherrymango.foodiehub.repository.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SiteUserService {
    private final SiteUserRepository siteUserRepository;

    // 회원 저장
    public Long save(AddUserRequest dto){

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("저장 권한: " + dto.getRole());

        return siteUserRepository.save(SiteUser.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword1()))// 패스워드 암호화 , 패스워드를 저장할때 시큐리티를 설정하며 패스워드 인코딩용으로 등록한 빈을 사용해서 암호화후에 저장
                .nickname(dto.getNickname())
                .name(dto.getName())
                .cellphone(dto.getCellphone())
                .role(String.valueOf(dto.getRole()))
                .build()).getId();
    }

    // 사장님(관리자) 저장
    public Long save_admin(AddAdminRequest dto){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("저장 권한: " + dto.getRole());

        return siteUserRepository.save(SiteUser.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword1()))// 패스워드 암호화 , 패스워드를 저장할때 시큐리티를 설정하며 패스워드 인코딩용으로 등록한 빈을 사용해서 암호화후에 저장
                .nickname(dto.getNickname())
                .name(dto.getName())
                .cellphone(dto.getCellphone())
                .role(String.valueOf(dto.getRole()))
                .businessno(dto.getBusinessno())
                .build()).getId();
    }

    // 전달 받은 유저 ID 로 유저를 검색해서 전달하는 메서드
    public SiteUser findById (Long userId){
        return siteUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
    }

    // 이메일을 입력받아 SiteUser 테이블에서 유저를 찾고, 없으면 예외 처리
    public Optional<SiteUser> findByEmail (String email){
        return siteUserRepository.findByEmail(email);
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
        return siteUserRepository.save(user);
    }

    // 닉네임 중복 검사를 수행하는 메서드
    public boolean isNicknameDuplicated(String nickname){
        return siteUserRepository.existsByNickname(nickname);
    }

    // 회원정보수정
    @Transactional // 트랜잭션 활성화
    public boolean updateUserProfile(SiteUserProfileRequest profileRequest, String profileImageUrl) {
        try {
            // 사용자 조회
            SiteUser user = siteUserRepository.findByEmail(profileRequest.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 닉네임 및 전화번호 수정
            user.setNickname(profileRequest.getNickname());
            user.setCellphone(profileRequest.getCellphone());
            // 프로필 이미지 경로 수정
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                user.setProfileImageUrl(profileImageUrl); // 새로 업로드된 이미지 경로 설정
            }

            // 비밀번호 변경
            if (profileRequest.getCurrentPassword() != null && !profileRequest.getCurrentPassword().isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // 인코더 직접 생성
                if (!encoder.matches(profileRequest.getCurrentPassword(), user.getPassword())) {
                    throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
                }
                String encodedPassword = encoder.encode(profileRequest.getNewPassword()); // 새 비밀번호 암호화
                user.setPassword(encodedPassword); // 비밀번호 변경
            }
            // JPA가 자동으로 변경 사항을 데이터베이스에 반영 (save() 호출 불필요)
            // 성공적으로 수정되면 true 반환
            return true;
        } catch (Exception e) {
            // 예외 발생 시 로그 출력 및 false 반환
            e.printStackTrace();
            return false;
        }
    }

}
