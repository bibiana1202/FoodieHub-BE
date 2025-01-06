package com.cherrymango.foodiehub.dto.user;

import com.cherrymango.foodiehub.domain.Role;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class SiteUserProfileResponse {

    private String nickname;

    private String email;

    private String cellphone;
    private Role role;
    private String profileimageurl;

//    private String currentPassword; // 현재 비밀번호
//
//    private String newPassword; // 새 비밀번호
//
//    private MultipartFile profileImage; // 업로드된 이미지
//
//    private String existingImageUrl; // 기존 이미지 URL

}
