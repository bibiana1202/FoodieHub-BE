package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.dto.MyPageStoreResponseDto;
import com.cherrymango.foodiehub.file.FileStore;
import com.cherrymango.foodiehub.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreApiController {
    final private FileStore fileStore;
    final private StoreService storeService;

    @GetMapping("/store-image/{filename}")
    public Resource downloadStoreImage(@PathVariable("filename") String filename) throws MalformedURLException {
        System.out.println("fileStore.getFullPath(filename)");
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/like/{userId}")
    public ResponseEntity<List<MyPageStoreResponseDto>> getMyPageStoreLikeList(@PathVariable("userId") Long userId) {
        List<MyPageStoreResponseDto> storeLikeList = storeService.getStoreLikeList(userId);
        return ResponseEntity.ok(storeLikeList);
    }

    @GetMapping("/favorite/{userId}")
    public ResponseEntity<List<MyPageStoreResponseDto>> getMyPageStoreFavoriteList(@PathVariable("userId") Long userId) {
        List<MyPageStoreResponseDto> storeFavoriteList = storeService.getStoreFavoriteList(userId);
        return ResponseEntity.ok(storeFavoriteList);
    }

}
