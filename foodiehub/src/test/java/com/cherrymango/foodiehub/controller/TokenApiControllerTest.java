package com.cherrymango.foodiehub.controller;

import com.cherrymango.foodiehub.config.jwt.JwtFactory;
import com.cherrymango.foodiehub.config.jwt.JwtProperties;
import com.cherrymango.foodiehub.domain.RefreshToken;
import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.dto.CreateAccessTokenRequest;
import com.cherrymango.foodiehub.repository.RefreshTokenRepository;
import com.cherrymango.foodiehub.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetup(){
        this.mockMvc= MockMvcBuilders.webAppContextSetup(context).build();
        userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken : 새로운 액세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        // given : 테스트 유저를 생성하고,jjwt라이브러리를 이용해 리프레시 토큰을 만들어 데이터베이스에 저장합니다. 토큰 생성 API 의 요청 본문에 리프레시 토큰을 포함하여 요청 객체를 생성합니다.
        final String url = "/api/token";

        SiteUser testUser = userRepository.save(SiteUser.builder()
                .email("user3@gmail.com")
                .password("{noop}test") // Spring Security의 NoOpPasswordEncoder를 사용
                .build());

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id",testUser.getId()))
                .build()
                .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(testUser.getId(),refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);
        final String requestBody =objectMapper.writeValueAsString(request);

        // when : 토큰 추가 API에 요청을 보냅니다. 이때 요청 타입은 JSON이며, given 절에서 미리 만들어둔 객체를 요청 본문으로 함께 보냅니다.
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));
        // then : 응답코드가 201 Created 인지 확인하고 응답으로 온 액세스 토큰이 비어있지 않은지 확인합니다.
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());


    }

}
