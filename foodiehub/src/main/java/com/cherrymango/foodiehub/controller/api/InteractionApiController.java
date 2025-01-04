package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.dto.FavoriteResponseDto;
import com.cherrymango.foodiehub.dto.LikeResponseDto;
import com.cherrymango.foodiehub.service.ReviewLikeService;
import com.cherrymango.foodiehub.service.StoreFavoriteService;
import com.cherrymango.foodiehub.service.StoreLikeService;
import com.cherrymango.foodiehub.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InteractionApiController {
    private final StoreFavoriteService storeFavoriteService;
    private final StoreLikeService storeLikeService;
    private final ReviewLikeService reviewLikeService;
    private final TokenUtil tokenUtil;

    @PostMapping("/store/favorite/{storeId}")
    public ResponseEntity<FavoriteResponseDto> toggleStoreFavorite(@PathVariable("storeId") Long storeId, Principal principal, HttpServletRequest request) {
        Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
        return ResponseEntity.ok(storeFavoriteService.toggleFavorite(storeId, userId));
    }

    @PostMapping("/store/like/{storeId}")
    public ResponseEntity<LikeResponseDto> toggleStoreLike(@PathVariable("storeId") Long storeId, Principal principal, HttpServletRequest request) {
        Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
        return ResponseEntity.ok(storeLikeService.toggleLike(storeId, userId));
    }

    /** 테스트를 위한 api */
    @PostMapping("/store/{storeId}/favorite/{userId}")
    public ResponseEntity<FavoriteResponseDto> toggleStoreFavoriteTest(@PathVariable("storeId") Long storeId, @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(storeFavoriteService.toggleFavorite(storeId, userId));
    }

    @PostMapping("/store/{storeId}/like/{userId}")
    public ResponseEntity<LikeResponseDto> toggleStoreLikeTest(@PathVariable("storeId") Long storeId, @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(storeLikeService.toggleLike(storeId, userId));
    }
    /** 테스트를 위한 api */

    @PostMapping("/review/{reviewId}/like")
    public ResponseEntity<LikeResponseDto> toggleReviewLike(@PathVariable("reviewId") Long reviewId, @RequestParam(value = "userId") Long userId) {
        return ResponseEntity.ok(reviewLikeService.toggleLike(reviewId, userId));
    }

    @DeleteMapping("/store/{storeId}/favorite")
    public ResponseEntity<Boolean> removeStoreFavorite(@PathVariable("storeId") Long storeId, Principal principal, HttpServletRequest request) {
        Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
        boolean result = storeFavoriteService.removeFavorite(storeId, userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/store/{storeId}/like")
    public ResponseEntity<Boolean> removeStoreLike(@PathVariable("storeId") Long storeId, Principal principal, HttpServletRequest request) {
        Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
        boolean result = storeLikeService.removeFavorite(storeId, userId);
        return ResponseEntity.ok(result);
    }
}
