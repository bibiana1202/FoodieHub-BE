package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
