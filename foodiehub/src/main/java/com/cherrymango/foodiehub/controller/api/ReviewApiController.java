package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.dto.*;
import com.cherrymango.foodiehub.file.FileStore;
import com.cherrymango.foodiehub.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewApiController {
    private final ReviewService reviewService;
    private final FileStore fileStore;

    // 이미지 없는 경우 처리는 화면에서 하거나 컨트롤러에서 하도록 선택 가능
    @GetMapping({"/profile-image", "/profile-image/{filename}"})
    public Resource downloadProfileImage(@PathVariable(value = "filename", required = false) String filename) throws MalformedURLException {
        String defaultImagePath = "classpath:/static/images/profile.png";
        System.out.println("filename = " + filename);
        if (filename == null || filename.trim().isEmpty()) {
            return new UrlResource(defaultImagePath);
        }
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/review-image/{filename}")
    public Resource downloadReviewImage(@PathVariable("filename") String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @PostMapping("/{userId}/{storeId}")
    public ResponseEntity<Long> addReview(@PathVariable("userId") Long userId, @PathVariable("storeId") Long storeId,
                                          @ModelAttribute @Valid AddReviewRequestDto addReviewRequestDto) {
        Long reviewId = reviewService.save(userId, storeId, addReviewRequestDto);
        return ResponseEntity.ok(reviewId);
    }

//    @GetMapping("/store/{storeId}")
//    public ResponseEntity<List<StoreReviewResponseDto>> findAllStoreReviews(@PathVariable("storeId") Long storeId) {
//        List<StoreReviewResponseDto> storeReviews = reviewService.findReviewsByStoreId(storeId);
//        return ResponseEntity.ok(storeReviews);
//    }

    // 가게 리뷰 목록
    @GetMapping("/store/{storeId}")
    public ResponseEntity<PagedResponseDto<StoreReviewResponseDto>> findStoreReviews(@PathVariable("storeId") Long storeId,
                                                                                     @RequestParam(value = "page", defaultValue = "0") int page) {
        PagedResponseDto<StoreReviewResponseDto> storeReviews = reviewService.findReviewsByStoreId(storeId, page);
        return ResponseEntity.ok(storeReviews);
    }

    // 마이페이지 리뷰 목록
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MyPageReviewResponseDto>> findAllMyPageReviews(@PathVariable("userId") Long userId) {
        List<MyPageReviewResponseDto> userReviews = reviewService.findReviewsByUserId(userId);
        return ResponseEntity.ok(userReviews);
    }

    // 리뷰 상세 정보 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<MyPageReviewResponseDto> findReview(@PathVariable("reviewId") Long reviewId) {
        MyPageReviewResponseDto review = reviewService.findReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    // 리뷰 수정 요청
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(@PathVariable("reviewId") Long reviewId,
                                             @ModelAttribute @Valid UpdateReviewRequestDto updateReviewRequestDto) {
        reviewService.updateReview(reviewId, updateReviewRequestDto);
        return ResponseEntity.ok().build();
    }

}
