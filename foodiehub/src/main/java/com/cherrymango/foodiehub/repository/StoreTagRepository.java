package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.StoreTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreTagRepository extends JpaRepository<StoreTag, Long> {
    // Optional<StoreTag> findByStoreAndTag(Store store, Tag tag);
}
