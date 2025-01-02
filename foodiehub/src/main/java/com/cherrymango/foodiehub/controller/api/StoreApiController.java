package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.domain.Category;
import com.cherrymango.foodiehub.dto.*;
import com.cherrymango.foodiehub.file.FileStore;
import com.cherrymango.foodiehub.service.StoreService;
import com.cherrymango.foodiehub.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StoreApiController {
    final private FileStore fileStore;
    final private StoreService storeService;
    private final TokenUtil tokenUtil;

    @GetMapping("/store-image/{filename}")
    public Resource downloadStoreImage(@PathVariable("filename") String filename) throws MalformedURLException {
        System.out.println("fileStore.getFullPath(filename)");
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/store/like/{userId}")
    public ResponseEntity<List<MyPageStoreResponseDto>> getMyPageStoreLikeList(@PathVariable("userId") Long userId) {
        List<MyPageStoreResponseDto> storeLikeList = storeService.getStoreLikeList(userId);
        return ResponseEntity.ok(storeLikeList);
    }

    @GetMapping("/store/favorite/{userId}")
    public ResponseEntity<List<MyPageStoreResponseDto>> getMyPageStoreFavoriteList(@PathVariable("userId") Long userId) {
        List<MyPageStoreResponseDto> storeFavoriteList = storeService.getStoreFavoriteList(userId);
        return ResponseEntity.ok(storeFavoriteList);
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

    // 가게 정보 저장
    /*
    *     @PostMapping("/save/{userId}")
    public String postSaveStore(@ModelAttribute @Valid AddStoreRequestDto addStoreRequestDto, @PathVariable("userId") Long userId) {
        storeService.register(userId, addStoreRequestDto);
        return "redirect:/";
    }
    * */
    @PostMapping("/store/save")
    public ResponseEntity<Long> storeSave(@ModelAttribute @Valid AddStoreRequestDto addStoreRequestDto, Principal principal, HttpServletRequest request){
        System.out.println("storeSave!!!!!!!!");
        try {
            Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
            System.out.println("userId = " + userId);
            Long storeId = storeService.register(userId, addStoreRequestDto);
            System.out.println("storeId = " + storeId);
            return ResponseEntity.ok(storeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
