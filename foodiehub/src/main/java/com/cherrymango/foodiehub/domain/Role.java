package com.cherrymango.foodiehub.domain;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role);
        } catch (IllegalArgumentException | NullPointerException e) {
            return ROLE_USER; // 기본값 설정
        }
    }
}
