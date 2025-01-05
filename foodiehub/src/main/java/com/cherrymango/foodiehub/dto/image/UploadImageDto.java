package com.cherrymango.foodiehub.dto.image;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UploadImageDto {

    private String uploadFileName;
    private String storeFileName;

    public UploadImageDto(String uploadFileName, String storeFIleName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFIleName;
    }
}
