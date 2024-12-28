package com.cherrymango.foodiehub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class StoreImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String uploadImageName;

    private String storeImageName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    //==생성 메소드==//
    public static StoreImage createStoreImage(String uploadImageName, String storeImageName){
        StoreImage storeImage = new StoreImage();
        storeImage.setUploadImageName(uploadImageName);
        storeImage.setStoreImageName(storeImageName);

        return storeImage;
    }

    public static StoreImage createStoreImage(String uploadImageName, String storeImageName, Store store){
        StoreImage storeImage = new StoreImage();
        storeImage.setUploadImageName(uploadImageName);
        storeImage.setStoreImageName(storeImageName);
        storeImage.setStore(store);

        return storeImage;
    }
}
