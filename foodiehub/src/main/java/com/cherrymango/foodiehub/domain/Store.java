package com.cherrymango.foodiehub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Check(constraints = "parking IN (0, 1)")
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SiteUser user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 100)
    private String intro;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private Integer parking;

    private String operationHours;

    private String lastOrder;

    @Column(nullable = false, length = 2000)
    private String content;

    private LocalDateTime registerDate;

    // orphanRemoval은 자식 엔티티가 다른 부모 엔티티에 의해 참조되고 있는 경우에도 삭제되지 않는다.
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreFavorite> storeFavorites = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreLike> storeLikes = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreTag> storeTags = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreImage> storeImageList = new ArrayList<>();

    //==연관관계 메소드==//
    public void addMenu(Menu menu) {
        menus.add(menu);
        menu.setStore(this);
    }

    public void addStoreTag(StoreTag storeTag) {
        storeTags.add(storeTag);
        storeTag.setStore(this);
    }

    public void addStoreImage(StoreImage storeImage) {
        storeImageList.add(storeImage);
        storeImage.setStore(this);
    }

    //==생성 메소드==//
    public static Store createStore(
            SiteUser user, String name, String intro, String phone, String address, Category category,
            Integer parking, String operationHours, String lastOrder, String content,
            List<Menu> menus, List<StoreTag> storeTags, List<StoreImage> storeImages
    ) {
        Store store = new Store();
        store.setUser(user);
        store.setName(name);
        store.setIntro(intro);
        store.setPhone(phone);
        store.setAddress(address);
        store.setCategory(category);
        store.setParking(parking);
        store.setOperationHours(operationHours);
        store.setLastOrder(lastOrder);
        store.setContent(content);
        store.setRegisterDate(LocalDateTime.now());

        // 메뉴 추가
        for (Menu menu : menus) {
            store.addMenu(menu);
        }

        // 태그 추가
        for (StoreTag storeTag : storeTags) {
            store.addStoreTag(storeTag);
        }

        // 이미지 추가
        for (StoreImage storeImage : storeImages) {
            store.addStoreImage(storeImage);
        }

        return store;
    }
}