package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.Review;
import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByStore(Store store);

    Page<Review> findByStore(Store store, Pageable pageable);

    List<Review> findByUser(SiteUser User);

    List<Review> findByUserOrderByCreateDateDesc(SiteUser user);

    List<Review> findByStoreOrderByCreateDateDesc(Store store);
}
