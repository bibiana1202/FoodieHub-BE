package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByUser(SiteUser user);
}
