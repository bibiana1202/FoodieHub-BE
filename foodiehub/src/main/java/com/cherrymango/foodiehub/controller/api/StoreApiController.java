package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.service.StoreFavoriteService;
import com.cherrymango.foodiehub.service.StoreLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreApiController {
    private final StoreFavoriteService storeFavoriteService;
    private final StoreLikeService storeLikeService;

    @PostMapping("/{storeId}/favorite")
    public ResponseEntity<String> toggleFavorite(@PathVariable("storeId") Long storeId, @RequestParam(value = "userId") Long userId) {
        boolean favorite = storeFavoriteService.toggleFavorite(storeId, userId);

        if (favorite) {
            return ResponseEntity.ok("Favorite");
        } else {
            return ResponseEntity.ok("Unfavorite");
        }
    }

    @PostMapping("/{storeId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable("storeId") Long storeId, @RequestParam(value = "userId") Long userId) {

        boolean like = storeLikeService.toggleLike(storeId, userId);

        if (like) {
            return ResponseEntity.ok("Like");
        } else {
            return ResponseEntity.ok("Unlike");
        }
    }
}
