package com.cherrymango.foodiehub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeResponseDto {

    private long likeCount;
    private boolean isLiked;

}
