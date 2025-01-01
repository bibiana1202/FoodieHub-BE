package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.domain.StoreLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
    // 특정 Store와 User에 대한 좋아요
    Optional<StoreLike> findByStoreAndUser(Store store, SiteUser user);

    // Id 기반 조회
    Optional<StoreLike> findByStoreIdAndUserId(Long storeId, Long userId);

    // 특정 Store에 대한 좋아요 개수 조회
    long countByStore(Store store);

    // 특정 Store에 User의 좋아요 여부 반환
    boolean existsByStoreAndUser(Store store, SiteUser user);

    // 특정 User의 좋아요 리스트, 최신순으로 정렬
    List<StoreLike> findByUserOrderByLikeTimeDesc(SiteUser user);
}
