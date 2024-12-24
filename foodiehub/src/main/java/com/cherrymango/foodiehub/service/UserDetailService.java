package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    //사용자 이름(email)으로 사용자의 정보를 가져오는 메서드
    @Override
    public SiteUser loadUserByUsername(String username){
        System.out.println("로그인 요청 email: " + username);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

}
