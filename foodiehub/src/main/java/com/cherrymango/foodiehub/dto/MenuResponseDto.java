package com.cherrymango.foodiehub.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuResponseDto {

    private Long id;
    private String name;
    private Integer price;

}
