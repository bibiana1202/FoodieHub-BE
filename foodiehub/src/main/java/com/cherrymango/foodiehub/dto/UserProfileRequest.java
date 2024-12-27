package com.cherrymango.foodiehub.dto;

import lombok.Data;

@Data // Getter, Setter, toString, equals, hashCode 자동 생성
public class UserProfileRequest {
    private String nickname;
    private String email;
    private String phone;
    private String currentPassword; // 현재 비밀번호
    private String newPassword; // 새 비밀번호
}
