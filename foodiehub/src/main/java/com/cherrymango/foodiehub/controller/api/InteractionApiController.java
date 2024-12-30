package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.dto.LikeResponseDto;
import com.cherrymango.foodiehub.service.ReviewLikeService;
import com.cherrymango.foodiehub.service.StoreFavoriteService;
import com.cherrymango.foodiehub.service.StoreLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InteractionApiController {
    private final StoreFavoriteService storeFavoriteService;
    private final StoreLikeService storeLikeService;
    private final ReviewLikeService reviewLikeService;

    @PostMapping("/store/{storeId}/favorite")
    public ResponseEntity<Boolean> toggleStoreFavorite(@PathVariable("storeId") Long storeId, @RequestParam(value = "userId") Long userId) {
        return ResponseEntity.ok(storeFavoriteService.toggleFavorite(storeId, userId));
    }

    @PostMapping("/store/{storeId}/like")
    public ResponseEntity<LikeResponseDto> toggleStoreLike(@PathVariable("storeId") Long storeId, @RequestParam(value = "userId") Long userId) {
        return ResponseEntity.ok(storeLikeService.toggleLike(storeId, userId));
    }

    @PostMapping("/review/{reviewId}/like")
    public ResponseEntity<LikeResponseDto> toggleReviewLike(@PathVariable("reviewId") Long reviewId, @RequestParam(value = "userId") Long userId) {
        return ResponseEntity.ok(reviewLikeService.toggleLike(reviewId, userId));
    }
}
