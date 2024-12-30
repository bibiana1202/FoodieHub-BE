package com.cherrymango.foodiehub.controller.api;

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
    public ResponseEntity<String> toggleStoreFavorite(@PathVariable("storeId") Long storeId, @RequestParam(value = "userId") Long userId) {
        boolean favorite = storeFavoriteService.toggleFavorite(storeId, userId);

        if (favorite) {
            return ResponseEntity.ok("Favorite");
        } else {
            return ResponseEntity.ok("Unfavorite");
        }
    }

    @PostMapping("/store/{storeId}/like")
    public ResponseEntity<String> toggleStoreLike(@PathVariable("storeId") Long storeId, @RequestParam(value = "userId") Long userId) {

        boolean like = storeLikeService.toggleLike(storeId, userId);

        if (like) {
            return ResponseEntity.ok("Like");
        } else {
            return ResponseEntity.ok("Unlike");
        }
    }

    @PostMapping("/review/{reviewId}/like")
    public ResponseEntity<String> toggleReviewLike(@PathVariable("reviewId") Long reviewId, @RequestParam(value = "userId") Long userId) {

        boolean like = reviewLikeService.toggleLike(reviewId, userId);

        if (like) {
            return ResponseEntity.ok("Like");
        } else {
            return ResponseEntity.ok("Unlike");
        }
    }
}
