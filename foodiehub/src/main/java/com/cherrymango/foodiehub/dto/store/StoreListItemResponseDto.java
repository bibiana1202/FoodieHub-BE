package com.cherrymango.foodiehub.dto.store;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreListItemResponseDto {

    private Long id;
    private String name;
    private String intro;
    private String content;
    private String image;
    private Double avgRating;

}
