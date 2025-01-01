package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.dto.*;
import com.cherrymango.foodiehub.file.FileStore;
import com.cherrymango.foodiehub.repository.SiteUserRepository;
import com.cherrymango.foodiehub.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewApiController {
    private final ReviewService reviewService;
    private final FileStore fileStore;
    private final SiteUserRepository siteUserRepository;

    // 디폴트 이미지
    @GetMapping("/image/default")
    public Resource getDefaultImage() throws MalformedURLException {
        return new UrlResource("classpath:/static/images/profile.png");
    }

    @GetMapping("/image/{filename}")
    public Resource downloadReviewImage(@PathVariable("filename") String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @PostMapping("/{userId}/{storeId}")
    public ResponseEntity<Long> addReview(@PathVariable("userId") Long userId, @PathVariable("storeId") Long storeId,
                                          @ModelAttribute @Valid AddReviewRequestDto addReviewRequestDto) {
        Long reviewId = reviewService.save(userId, storeId, addReviewRequestDto);
        return ResponseEntity.ok(reviewId);
    }

    @GetMapping("/admin/store/{storeId}")
    public ResponseEntity<List<StoreReviewResponseDto>> findStoreReviews(@PathVariable("storeId") Long storeId) {
        List<StoreReviewResponseDto> storeReviews = reviewService.getStoreReviews(storeId);
        return ResponseEntity.ok(storeReviews);
    }

    // 가게 상세 리뷰 목록
    @GetMapping("/store/{storeId}")
    public ResponseEntity<PagedResponseDto<StoreReviewResponseDto>> findPagedStoreReviews(@PathVariable("storeId") Long storeId,
                                                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                     Principal principal) {
        // Principal에서 사용자 정보 가져오기
        Long userId = null;
        if (principal != null) {
            String email = principal.getName(); // 사용자명 가져오기
            SiteUser user = siteUserRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            userId = user.getId(); // 사용자 ID 가져오기
        }

        // OAuth2 로그인 처리
//        if (principal != null && principal instanceof OAuth2AuthenticationToken) {
//            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) principal;
//            Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
//            email = (String) attributes.get("email");
//            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+email);
//        } else {
//            // 폼 로그인 처리
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication != null) {
//                email = authentication.getName();//authentication.getName(); // 사용자 이름 가져오기
//            }
//        }

        PagedResponseDto<StoreReviewResponseDto> storeReviews = reviewService.getPagedStoreReviews(storeId, page, userId);
        // PagedResponseDto<StoreReviewResponseDto> storeReviews = reviewService.getPagedStoreReviews(storeId, page, 1L);
        return ResponseEntity.ok(storeReviews);
    }

    // 마이페이지 리뷰 목록
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MyPageReviewResponseDto>> findAllMyPageReviews(@PathVariable("userId") Long userId) {
        List<MyPageReviewResponseDto> userReviews = reviewService.findReviewsByUser(userId);
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

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

}
