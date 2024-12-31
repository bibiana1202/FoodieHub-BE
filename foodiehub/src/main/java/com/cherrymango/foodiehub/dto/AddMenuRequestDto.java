package com.cherrymango.foodiehub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMenuRequestDto {

    @NotEmpty
    private String name;
    @NotNull
    @Positive
    private Integer price;

}
