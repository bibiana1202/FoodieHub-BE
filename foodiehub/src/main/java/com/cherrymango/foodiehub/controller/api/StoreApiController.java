package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.domain.Category;
import com.cherrymango.foodiehub.dto.MyPageStoreResponseDto;
import com.cherrymango.foodiehub.dto.MyStoreResponseDto;
import com.cherrymango.foodiehub.dto.StoreListItemResponseDto;
import com.cherrymango.foodiehub.service.StoreService;
import com.cherrymango.foodiehub.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StoreApiController {
    final private StoreService storeService;
    final private TokenUtil tokenUtil;

    @GetMapping("/store/like")
    public ResponseEntity<List<MyPageStoreResponseDto>> getMyPageStoreLikeList(Principal principal, HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
            List<MyPageStoreResponseDto> storeLikeList = storeService.getStoreLikeList(userId);
            return ResponseEntity.ok(storeLikeList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/store/favorite")
    public ResponseEntity<List<MyPageStoreResponseDto>> getMyPageStoreFavoriteList(Principal principal, HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
            List<MyPageStoreResponseDto> storeFavoriteList = storeService.getStoreFavoriteList(userId);
            return ResponseEntity.ok(storeFavoriteList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/mystore/details/{userId}")
    public ResponseEntity<MyStoreResponseDto> getMyStoreDetails(@PathVariable("userId") Long userId) {
        MyStoreResponseDto myStoreDetails = storeService.getMyStoreDetails(userId);

        if (myStoreDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(myStoreDetails);
    }

    @GetMapping("/store/category/{category}")
    public ResponseEntity<List<StoreListItemResponseDto>> getStoreByCategory(@PathVariable("category") Category category,
                                                                             @RequestParam(value = "limit", required = false, defaultValue = "0") int limit) {
        List<StoreListItemResponseDto> stores = storeService.getStoresByCategory(category, limit);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/store/tag/{tag}")
    public ResponseEntity<List<StoreListItemResponseDto>> getStoreByTag(@PathVariable("tag") String tag,
                                                                             @RequestParam(value = "limit", required = false, defaultValue = "0") int limit) {
        List<StoreListItemResponseDto> stores = storeService.getStoresByTag(tag, limit);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/store/all")
    public ResponseEntity<List<StoreListItemResponseDto>> getAllStores(@RequestParam(value = "limit", required = false, defaultValue = "0") int limit) {
        List<StoreListItemResponseDto> stores = storeService.getAllStores(limit);
        return ResponseEntity.ok(stores);
    }

}
