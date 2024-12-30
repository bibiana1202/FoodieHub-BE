package com.cherrymango.foodiehub.dto;

import com.cherrymango.foodiehub.domain.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class AddStoreRequestDto { // StoreForm, vaildation

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
    @Valid // 내부 객체의 유효성 검사 활성화
    private List<AddMenuRequestDto> menus;
    private List<MultipartFile> images;
    private List<String> tags;

}

