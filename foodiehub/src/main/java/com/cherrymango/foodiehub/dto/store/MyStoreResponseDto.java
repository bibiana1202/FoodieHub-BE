package com.cherrymango.foodiehub.dto.store;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyStoreResponseDto {

    private Long id;
    private String name;
    private Double averageTotalRating; 
    private Double averageTasteRating;
    private Double averagePriceRating;
    private Double averageCleanRating;
    private Double averageFriendlyRating;

}
