package com.cherrymango.foodiehub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
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
    private Double avgRating;

    @Column(nullable = false)
    private Integer tasteRating;

    @Column(nullable = false)
    private Integer priceRating;

    @Column(nullable = false)
    private Integer cleanRating;

    @Column(nullable = false)
    private Integer friendlyRating;

    private String storeImageName;

    //==생성 메소드==//
    public static Review createReview(Store store, SiteUser user, String content, LocalDateTime createDate,
                                      Integer tasteRating, Integer priceRating, Integer cleanRating, Integer friendlyRating,
                                      String storeImageName) {
        Review review = new Review();

        review.setStore(store);
        review.setUser(user);
        review.setContent(content);
        review.setCreateDate(createDate);
        review.setTasteRating(tasteRating);
        review.setPriceRating(priceRating);
        review.setCleanRating(cleanRating);
        review.setFriendlyRating(friendlyRating);
        review.setStoreImageName(storeImageName);
        review.setAvgRating((tasteRating + priceRating + cleanRating + friendlyRating) / 4.0);

        return review;
    }
}
