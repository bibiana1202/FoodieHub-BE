package com.cherrymango.foodiehub.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SiteUser user;

    private String content;

    // 리뷰 reviewLikes.size만 필요
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewLike> reviewLikes = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @Column(nullable = false)
    private Integer tasteRating;

    @Column(nullable = false)
    private Integer priceRating;

    @Column(nullable = false)
    private Integer cleanRating;

    @Column(nullable = false)
    private Integer friendlyRating;

    private String imgUrl;

}
