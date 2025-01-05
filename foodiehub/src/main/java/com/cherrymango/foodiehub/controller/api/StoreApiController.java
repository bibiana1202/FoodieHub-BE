package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.domain.Category;
import com.cherrymango.foodiehub.dto.*;
import com.cherrymango.foodiehub.service.StoreService;
import com.cherrymango.foodiehub.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/store/detail/{storeId}")
    public ResponseEntity<StoreDetailResponseDto> getStoreDetails(@PathVariable("storeId") Long storeId, Principal principal, HttpServletRequest request) {
        Long userId = tokenUtil.getSiteUserIdOrNull(principal, request);
        StoreDetailResponseDto storeDetails = storeService.getStoreDetails(storeId, userId);
        return ResponseEntity.ok(storeDetails);
    }

    // 가게 정보 저장 (박혜정 2025-01-02)
    @PostMapping("/store/save")
    public ResponseEntity<Long> storeSave(@ModelAttribute @Valid AddStoreRequestDto addStoreRequestDto, Principal principal, HttpServletRequest request){
        try {
            Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
            Long storeId = storeService.register(userId, addStoreRequestDto);
            return ResponseEntity.ok(storeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // 내 식당 확인 (박혜정 2025-01-03)
    @GetMapping("/mystore/details")
    public ResponseEntity<?> getMyStoreDetails(Principal principal, HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
            MyStoreResponseDto myStoreDetails = storeService.getMyStoreDetails(userId);
            if (myStoreDetails == null) {
                return ResponseEntity.ok(Collections.singletonMap("isStore", false));
            }
//            return ResponseEntity.ok(myStoreDetails);
            return ResponseEntity.ok(Map.of(
                    "isStore", true,
                    "details", myStoreDetails
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "권한이 없습니다."));
        }
    }

    // 식당 정보 수정 GET (박혜정 2025-01-03)
    @GetMapping("/store/update")
    public ResponseEntity<?> getUpdateStore(Principal principal, HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
            Long storeId = storeService.getStoreIdByUserId(userId); // userId로 storeId 찾기
            UpdateStoreDetailDto store = storeService.getUpdateDetails(storeId);
            if (store == null) {
                return ResponseEntity.ok(Collections.singletonMap("isStore", false));
            }
            return ResponseEntity.ok(Map.of(
                    "isStore", true,
                    "store", store
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "권한이 없습니다."));
        }
    }

    // 식당 정보 수정 POST (박혜정 2025-01-03)
    @PutMapping("/store/update")
    public ResponseEntity<Long>  postUpdateStore(@RequestBody @Valid UpdateStoreRequestDto updateStoreRequestDto, Principal principal, HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
            Long storeId = storeService.getStoreIdByUserId(userId); // userId로 storeId 찾기
            storeService.update(storeId, updateStoreRequestDto);
            return ResponseEntity.ok(storeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
