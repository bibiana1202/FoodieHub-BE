package com.cherrymango.foodiehub.dto.FavoriteLike;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeResponseDto {

    private long likeCount;
    private boolean liked;

}
