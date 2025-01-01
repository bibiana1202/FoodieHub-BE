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

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreTag> storeTags = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
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

    // favorite 연관 메소드, 토글 메소드에서 save하고 따로 사용하지 않으면 필요 없음
    public void addStoreFavorite(StoreFavorite storeFavorite) {
        this.storeFavorites.add(storeFavorite);
    }

    public void removeStoreFavorite(StoreFavorite storeFavorite) {
        this.storeFavorites.remove(storeFavorite);
    }

    //==생성 메소드==//

}