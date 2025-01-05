package com.cherrymango.foodiehub.dto.FavoriteLike;

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
