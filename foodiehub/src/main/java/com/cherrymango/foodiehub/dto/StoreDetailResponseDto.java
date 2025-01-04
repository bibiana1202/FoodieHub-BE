package com.cherrymango.foodiehub.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StoreDetailResponseDto {

    private Long id;
    private String name;
    private String intro;
    private String phone;
    private String address;
    private String category;
    private Integer parking;
    private String operationHours;
    private String lastOrder;
    private String content;
    private LocalDateTime registerDate;
    private List<MenuResponseDto> menus; // new MenuResponseDto(null, , )
    private List<String> images;
    private List<String> tags;

    // 전체 리뷰 평균 별점 소수점 첫번째 자리
    private Double avgRating;

    private Integer likes;
    private Integer favorites;

    private Boolean isLiked;
    private Boolean isFavorite;

}