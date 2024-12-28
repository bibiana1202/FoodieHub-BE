package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.Menu;
import com.cherrymango.foodiehub.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByStore(Store store);
}
