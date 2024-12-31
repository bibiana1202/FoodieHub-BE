package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.domain.StoreFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreFavoriteRepository extends JpaRepository<StoreFavorite, Long> {
    Optional<StoreFavorite> findByStoreAndUser(Store store, SiteUser user);

    boolean existsByStoreAndUser(Store store, SiteUser user);

    List<StoreFavorite> findByUser(SiteUser user);

    List<StoreFavorite> findByUserOrderByFavoriteTimeDesc(SiteUser user);
}
