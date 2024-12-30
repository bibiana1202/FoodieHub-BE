package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.domain.StoreLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
    // 특정 Store와 User에 대한 좋아요
    Optional<StoreLike> findByStoreAndUser(Store store, SiteUser user);
    // 특정 Store에 대한 좋아요 개수 조회
    long countByStore(Store store);
}
