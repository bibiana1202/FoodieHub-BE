package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.domain.Menu;
import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.dto.*;
import com.cherrymango.foodiehub.repository.StoreRepository;
import com.cherrymango.foodiehub.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestApiController {
    private final StoreFavoriteService storeFavoriteService;
    private final StoreLikeService storeLikeService;
    private final ReviewLikeService reviewLikeService;
    private final ReviewService reviewService;
    private final MenuService menuService;
    private final StoreRepository storeRepository;
    private final StoreImageService storeImageService;

    /** Like, Favorite Test Api */
    @PostMapping("/api/store/{storeId}/favorite/{userId}")
    public ResponseEntity<FavoriteResponseDto> toggleStoreFavoriteTest(@PathVariable("storeId") Long storeId, @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(storeFavoriteService.toggleFavorite(storeId, userId));
    }

    @PostMapping("/api/store/{storeId}/like/{userId}")
    public ResponseEntity<LikeResponseDto> toggleStoreLikeTest(@PathVariable("storeId") Long storeId, @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(storeLikeService.toggleLike(storeId, userId));
    }

    @PostMapping("/api/review/{reviewId}/like/{userId}")
    public ResponseEntity<LikeResponseDto> toggleReviewLikeTest(@PathVariable("reviewId") Long reviewId, @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(reviewLikeService.toggleLike(reviewId, userId));
    }

    /** Add Review Test Api */
    @PostMapping("/api/user/{userId}/store/{storeId}")
    public ResponseEntity<Long> addReviewTest(@PathVariable("userId") Long userId, @PathVariable("storeId") Long storeId,
                                              @ModelAttribute @Valid AddReviewRequestDto addReviewRequestDto) {
        Long reviewId = reviewService.save(userId, storeId, addReviewRequestDto);
        return ResponseEntity.ok(reviewId);
    }

    /** Add Menu, Find All Menus Test Api, 메뉴 로그인 없이 추가하기 위한 Api */
    @PostMapping("/api/store/menu/{storeId}")
    public ResponseEntity<MenuResponseDto> addMenu(@PathVariable("storeId") Long storeId, @RequestBody @Valid AddMenuRequestDto request) {
        Long id = menuService.save(storeId, request);
        Menu findMenu = menuService.findOne(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MenuResponseDto(findMenu.getId(), findMenu.getName(), findMenu.getPrice()));
    }

    @GetMapping("/api/store/menu/{storeId}")
    public ResponseEntity<List<MenuResponseDto>> findAllMenus(@PathVariable("storeId") Long storeId) {
        Store store = storeRepository.findById(storeId).get();
        List<MenuResponseDto> menus = menuService.findMenus(store).stream()
                .map(menu -> new MenuResponseDto(menu.getId(), menu.getName(), menu.getPrice()))
                .toList();
        return ResponseEntity.ok()
                .body(menus);
    }

    /** Upload Image, Find All Images, 식당 사진 로그인 없이 추가하기 위한 Api */
    @PostMapping("/api/store/images/{storeId}")
    public ResponseEntity<Long> uploadImage(@PathVariable("storeId") Long storeId, @RequestParam("image") MultipartFile image) {
        Long imageId = storeImageService.save(storeId, image);
        return ResponseEntity.ok(imageId);
    }

    @GetMapping("/api/store/images/{storeId}")
    public ResponseEntity<List<StoreImageResponseDto>> findAllImages(@PathVariable("storeId") Long storeId) {
        Store store = storeRepository.findById(storeId).get();
        List<StoreImageResponseDto> images = storeImageService.findImages(store).stream()
                .map(image -> new StoreImageResponseDto(image.getId(), image.getStoreImageName()))
                .toList();

        return ResponseEntity.ok(images);
    }

}
