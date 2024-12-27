package com.cherrymango.foodiehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, toString, equals, hashCode 자동 생성
@AllArgsConstructor // 모든 필드를 포함하는 생성자 생성
@NoArgsConstructor  // 기본 생성자 생성
public class ApiResponse {
    private boolean success;
    private String message;
}
