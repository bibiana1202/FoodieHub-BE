package com.cherrymango.foodiehub.dto.store;

import com.cherrymango.foodiehub.domain.Category;
import com.cherrymango.foodiehub.dto.menu.AddMenuRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class AddStoreRequestDto {

    @NotEmpty(message = "가게 이름은 필수 입력 항목입니다.")
    private String name;
    @NotEmpty(message = "한줄 소개는 필수 입력 항목입니다.")
    @Size(max = 100)
    private String intro;
    @NotEmpty(message = "전화번호는 필수 입력 항목입니다.")
    private String phone;
    @NotEmpty(message = "주소는 필수 입력 항목입니다.")
    private String address;
    @NotNull(message = "카테고리는 필수 입력 항목입니다.")
    private Category category;
    @NotNull(message = "주차 가능 여부는 필수 입력 항목입니다. (0: 불가능, 1: 가능)")
    private Integer parking;
    private String operationHours;
    private String lastOrder;
    @NotEmpty(message = "상세 설명은 필수 입력 항목입니다.")
    @Size(max = 2000, message = "상세 설명은 최대 2000자까지 입력 가능합니다.")
    private String content;
    @Valid // 내부 객체의 유효성 검사 활성화
    private List<AddMenuRequestDto> menus;
    private List<MultipartFile> images;
    private List<String> tags;

}

