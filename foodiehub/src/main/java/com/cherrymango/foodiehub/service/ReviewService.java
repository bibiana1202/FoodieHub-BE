package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.domain.Review;
import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.dto.image.UploadImageDto;
import com.cherrymango.foodiehub.dto.review.AddReviewRequestDto;
import com.cherrymango.foodiehub.dto.review.MyPageReviewResponseDto;
import com.cherrymango.foodiehub.dto.review.StoreReviewResponseDto;
import com.cherrymango.foodiehub.dto.review.UpdateReviewRequestDto;
import com.cherrymango.foodiehub.dto.review.PagedResponseDto;
import com.cherrymango.foodiehub.file.FileStore;
import com.cherrymango.foodiehub.repository.ReviewLikeRepository;
import com.cherrymango.foodiehub.repository.ReviewRepository;
import com.cherrymango.foodiehub.repository.SiteUserRepository;
import com.cherrymango.foodiehub.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final SiteUserRepository siteUserRepository;
    private final FileStore fileStore;
    private final ReviewLikeRepository reviewLikeRepository;

    public Long save(Long userId, Long storeId, AddReviewRequestDto addReviewRequestDto) {
        SiteUser user = siteUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + storeId));

        Optional<String> storeImageName = Optional.ofNullable(addReviewRequestDto.getImage())
                .map(fileStore::storeFile)
                .map(UploadImageDto::getStoreFileName);

        Review review = Review.createReview(store, user, addReviewRequestDto.getContent(), LocalDateTime.now(),
                addReviewRequestDto.getTasteRating(), addReviewRequestDto.getPriceRating(),
                addReviewRequestDto.getCleanRating(), addReviewRequestDto.getFriendlyRating(), storeImageName.orElse(null));

        reviewRepository.save(review);

        return review.getId();
    }

    // 스토어 리뷰, 사용자 정보(닉네임, 프로필) 포함 반환
    public List<StoreReviewResponseDto> getStoreReviews(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + storeId));

        return reviewRepository.findByStoreOrderByCreateDateDesc(store).stream()
                .map(review -> new StoreReviewResponseDto(
                        review.getUser().getNickname(),
                        review.getUser().getProfileImageUrl(),
                        review.getId(),
                        roundToFirstDecimal(review.getAvgRating()),
                        review.getTasteRating(),
                        review.getPriceRating(),
                        review.getCleanRating(),
                        review.getFriendlyRating(),
                        review.getCreateDate(),
                        review.getContent(),
                        review.getStoreImageName(),
                        review.getReviewLikes().size(),
                        null
                ))
                .toList();
    }

    // 페이징 처리된 스토어 리뷰
    public PagedResponseDto<StoreReviewResponseDto> getPagedStoreReviews(Long storeId, int page, Long userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + storeId));

        // userId가 null이 아니면 사용자 객체 조회
        SiteUser user;
        if (userId != null) {
            user = siteUserRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } else {
            user = null;
        }

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 4, Sort.by(sorts)); // 4개씩, 최신순
        Page<Review> reviewsPage = reviewRepository.findByStore(store, pageable);

        return new PagedResponseDto<>(reviewsPage
                .map(review -> {
                    boolean isLiked = user != null && reviewLikeRepository.existsByReviewAndUser(review, user);
                    return new StoreReviewResponseDto(
                            review.getUser().getNickname(),
                            review.getUser().getProfileImageUrl(),
                            review.getId(),
                            roundToFirstDecimal(review.getAvgRating()),
                            review.getTasteRating(),
                            review.getPriceRating(),
                            review.getCleanRating(),
                            review.getFriendlyRating(),
                            review.getCreateDate(),
                            review.getContent(),
                            review.getStoreImageName(),
                            review.getReviewLikes().size(),
                            isLiked);
                }));
    }

    // 마이페이지 리뷰, 식당 이름 포함 반환
    public List<MyPageReviewResponseDto> findReviewsByUser(Long userId) {
        SiteUser user = siteUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return reviewRepository.findByUserOrderByCreateDateDesc(user).stream()
                .map(review -> new MyPageReviewResponseDto(
                        review.getStore().getName(), // Store 이름
                        review.getId(),
                        roundToFirstDecimal(review.getAvgRating()), // 평균 별점 소수점 첫째 자리로 반환
                        review.getTasteRating(),
                        review.getPriceRating(),
                        review.getCleanRating(),
                        review.getFriendlyRating(),
                        review.getCreateDate(),
                        review.getContent(),
                        review.getStoreImageName() // 리뷰 이미지 URL
                ))
                .toList();
    }

    private Double roundToFirstDecimal(Double value) {
        return value != null ? Math.round(value * 10) / 10.0 : null; // 소수점 첫 번째 자리로 반올림
    }

    // 마이페이지 리뷰 상세 조회
    @Transactional(readOnly = true)
    public MyPageReviewResponseDto findReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));

        return new MyPageReviewResponseDto(
                review.getStore().getName(),
                review.getId(),
                roundToFirstDecimal(review.getAvgRating()),
                review.getTasteRating(),
                review.getPriceRating(),
                review.getCleanRating(),
                review.getFriendlyRating(),
                review.getCreateDate(),
                review.getContent(),
                review.getStoreImageName()
        );
    }

    // 리뷰 수정
    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequestDto request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));

        review.setTasteRating(request.getTasteRating());
        review.setPriceRating(request.getPriceRating());
        review.setCleanRating(request.getCleanRating());
        review.setFriendlyRating(request.getFriendlyRating());
        review.setAvgRating((request.getTasteRating() + request.getPriceRating() + request.getCleanRating() + request.getFriendlyRating()) / 4.0);
        review.setContent(request.getContent());
        review.setModifyDate(LocalDateTime.now());

        // 이미지 처리
        if (request.isDeleteImage()) { // boolean의 경우 isDeleteImage(), Boolean의 경우 getDeleteImage()
            if (review.getStoreImageName() != null) { // 기존 이미지 삭제
                fileStore.deleteFile(review.getStoreImageName());
                review.setStoreImageName(null);
            }
        } else if (request.getImage() != null && !request.getImage().isEmpty()) { // 새 이미지 저장
            String newImageName = fileStore.storeFile(request.getImage()).getStoreFileName();
            if (review.getStoreImageName() != null) { // 기존 이미지 삭제
                fileStore.deleteFile(review.getStoreImageName());
            }
            review.setStoreImageName(newImageName);
        }
    }

    public boolean deleteReview(Long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            if (review.getStoreImageName() != null) {
                fileStore.deleteFile(review.getStoreImageName());
            }
            reviewRepository.deleteById(reviewId);
            return true;
        }
        return false;
    }

}
