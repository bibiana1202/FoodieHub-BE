package com.cherrymango.foodiehub.dto;

import com.cherrymango.foodiehub.domain.Category;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateStoreRequestDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String intro;
    @NotEmpty
    private String phone;
    @NotEmpty
    private String address;
    @NotNull
    private Category category;
    @NotNull
    private Integer parking;
    private String operationHours;
    private String lastOrder;
    @NotEmpty
    private String content;
    private List<String> tags;

}

