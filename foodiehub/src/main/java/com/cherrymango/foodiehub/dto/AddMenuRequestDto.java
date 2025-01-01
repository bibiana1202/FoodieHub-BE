package com.cherrymango.foodiehub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMenuRequestDto {

    @NotEmpty(message = "메뉴 이름은 필수 입력 항목입니다.")
    private String name;
    @NotNull(message = "메뉴 가격은 필수 입력 항목입니다.")
    @Positive
    private Integer price;

}
