package com.cherrymango.foodiehub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Setter
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Store store;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreFavorite> storeFavorites = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreLike> storeLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Review> reviews = new ArrayList<>();

    // private List<ReviewLike> reviewLikes = new ArrayList<>();

    //==연관관계 메소드==//
    // favorite 연관 메소드, 토글 메소드에서 save하고 따로 사용하지 않으면 필요 없음
    public void addStoreFavorite(StoreFavorite storeFavorite) {
        this.storeFavorites.add(storeFavorite);
    }

    public void removeStoreFavorite(StoreFavorite storeFavorite) {
        this.storeFavorites.remove(storeFavorite);
    }

}
