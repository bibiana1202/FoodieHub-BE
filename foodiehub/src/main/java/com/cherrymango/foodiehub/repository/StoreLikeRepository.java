package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.domain.StoreLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {
    Optional<StoreLike> findByStoreAndUser(Store store, SiteUser user);
}
