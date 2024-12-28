package com.cherrymango.foodiehub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MenuResponseDto {

    private Long id;
    private String name;
    private Integer price;

}
