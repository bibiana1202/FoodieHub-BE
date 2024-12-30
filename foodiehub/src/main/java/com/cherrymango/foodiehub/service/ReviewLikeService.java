package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.domain.Review;
import com.cherrymango.foodiehub.domain.ReviewLike;
import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.repository.ReviewLikeRepository;
import com.cherrymango.foodiehub.repository.ReviewRepository;
import com.cherrymango.foodiehub.repository.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewRepository reviewRepository;
    private final SiteUserRepository siteUserRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    // 좋아요 토글
    public boolean toggleLike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Review not found"));
        SiteUser user = siteUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<ReviewLike> existingLike = reviewLikeRepository.findByReviewAndUser(review, user);

        if (existingLike.isPresent()) { // 좋아요 취소
            reviewLikeRepository.delete(existingLike.get());
            return false;
        } else { // 좋아요 추가
            ReviewLike reviewLike = ReviewLike.builder()
                    .review(review)
                    .user(user)
                    .likeTime(LocalDateTime.now()).build();

            reviewLikeRepository.save(reviewLike);

            return true;
        }
    }
}
