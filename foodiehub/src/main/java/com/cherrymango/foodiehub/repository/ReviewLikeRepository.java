package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.Review;
import com.cherrymango.foodiehub.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReviewAndUser(Review review, SiteUser user);

    long countByReview(Review review);

    boolean existsByReviewAndUser(Review review, SiteUser user);
}
