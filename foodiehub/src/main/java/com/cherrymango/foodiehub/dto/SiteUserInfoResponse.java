package com.cherrymango.foodiehub.dto;

import com.cherrymango.foodiehub.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SiteUserInfoResponse {
//    private Long userid;
//    private String cellphone;
//    private String email;
//    private String name;
    private String nickname;
//    private String provider;
    private Role role;
//    private String businessno;
    private String profileimageurl;
}
