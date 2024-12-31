package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.domain.StoreLike;
import com.cherrymango.foodiehub.dto.LikeResponseDto;
import com.cherrymango.foodiehub.repository.SiteUserRepository;
import com.cherrymango.foodiehub.repository.StoreLikeRepository;
import com.cherrymango.foodiehub.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreLikeService {
    private final StoreRepository storeRepository;
    private final SiteUserRepository siteUserRepository;
    private final StoreLikeRepository storeLikeRepository;

    // 좋아요 토글
    public LikeResponseDto toggleLike(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("Store not found"));
        SiteUser user = siteUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<StoreLike> existingLike = storeLikeRepository.findByStoreAndUser(store, user);
        boolean isLiked;

        if (existingLike.isPresent()) { // 좋아요 취소
            storeLikeRepository.delete(existingLike.get());
            isLiked = false;
        } else { // 좋아요 추가
            StoreLike storeLike = StoreLike.builder()
                    .store(store)
                    .user(user)
                    .likeTime(LocalDateTime.now()).build();

            storeLikeRepository.save(storeLike);
            isLiked = true;
        }

        long likeCount = storeLikeRepository.countByStore(store);

        return new LikeResponseDto(likeCount, isLiked);
    }
}
