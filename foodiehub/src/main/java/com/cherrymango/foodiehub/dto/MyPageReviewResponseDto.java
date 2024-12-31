package com.cherrymango.foodiehub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MyPageReviewResponseDto {

    private String storeName;

    private Long id;
    private Double avgRating;
    private Integer tasteRating;
    private Integer priceRating;
    private Integer cleanRating;
    private Integer friendlyRating;
    private LocalDateTime createDate;
    private String content;
    private String reviewImage;

}
