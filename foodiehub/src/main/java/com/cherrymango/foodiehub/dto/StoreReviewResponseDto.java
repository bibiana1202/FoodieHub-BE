package com.cherrymango.foodiehub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class StoreReviewResponseDto {

    private String nickname;
    private String profileImage;

    private Long id;
    private Double avgRating; // 소수점 첫번째 자리로 반환
    private Integer tasteRating;
    private Integer priceRating;
    private Integer cleanRating;
    private Integer friendlyRating;
    private LocalDateTime createDate;
    private String content;
    private String reviewImage;
    private Integer likes;

}
