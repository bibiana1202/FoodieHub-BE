package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.dto.AddUserRequest;
import com.cherrymango.foodiehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Long save(AddUserRequest dto){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(SiteUser.builder()
                .email(dto.getEmail())
                // 패스워드 암호화 , 패스워드를 저장할때 시큐리티를 설정하며 패스워드 인코딩용으로 등록한 빈을 사용해서 암호화후에 저장
                .password(encoder.encode(dto.getPassword()))
                .build()).getId();
    }
}
