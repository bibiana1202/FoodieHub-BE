package com.cherrymango.foodiehub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserProfileRequest {

    @NotBlank(message = "닉네임 은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    private String cellphone;

    private String currentPassword; // 현재 비밀번호

    private String newPassword; // 새 비밀번호

    private MultipartFile profileImage; // 파일 필드

}
