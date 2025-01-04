package com.cherrymango.foodiehub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FavoriteResponseDto {

    private long favoriteCount;
    private boolean favorited;

}
