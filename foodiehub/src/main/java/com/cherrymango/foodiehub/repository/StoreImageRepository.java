package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.domain.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {
    List<StoreImage> findByStore(Store store);
}
