package com.cherrymango.foodiehub.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageStoreResponseDto {

    private Long id;
    private String name;
    private String intro;
    private String category;
    private String content;
    private String image;

}
