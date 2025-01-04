package com.cherrymango.foodiehub.repository;

import com.cherrymango.foodiehub.domain.Category;
import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.domain.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByUser(SiteUser user);

    List<Store> findAllByOrderByRegisterDateDesc(Pageable pageable);

    // 특정 카테고리 등록 최신순 정렬 반환
    List<Store> findByCategoryOrderByRegisterDateDesc(Category category, Pageable pageable);

    // 특정 태그 등록 최신순 정렬 반환
    @Query("SELECT s FROM Store s JOIN s.storeTags st WHERE st.tag.name = :tagName ORDER BY s.registerDate DESC")
    List<Store> findByTagNameOrderByRegisterDateDesc(@Param("tagName") String tagName, Pageable pageable);
}
