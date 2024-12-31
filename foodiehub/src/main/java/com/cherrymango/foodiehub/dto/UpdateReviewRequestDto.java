package com.cherrymango.foodiehub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateReviewRequestDto {

    @NotNull
    @Positive
    private Integer tasteRating;
    @NotNull
    @Positive
    private Integer priceRating;
    @NotNull
    @Positive
    private Integer cleanRating;
    @NotNull
    @Positive
    private Integer friendlyRating;
    @NotEmpty
    private String content;
    private MultipartFile image;
    private boolean deleteImage; // 이미지 삭제 여부

}
