package com.cherrymango.foodiehub.dto.store;

import com.cherrymango.foodiehub.domain.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UpdateStoreDetailResponseDto {

    private Long id;
    private String name;
    private String intro;
    private String phone;
    private String address;
    private Category category;
    private Integer parking;
    private String operationHours;
    private String lastOrder;
    private String content;
    private LocalDateTime registerDate;
    private List<String> tags;

}