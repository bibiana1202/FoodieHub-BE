package com.cherrymango.foodiehub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mypage")
public class MyPageViewController {
    @GetMapping("/favorites")
    public ResponseEntity<List<Map<String, String>>> getFavorites() {
        List<Map<String, String>> favorites = new ArrayList<>();
        favorites.add(Map.of("name", "파볼라1", "description", "설명입니다.", "image", "/default-image.png"));
        favorites.add(Map.of("name", "쥬안1", "description", "설명입니다.", "image", "/default-image.png"));
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<Map<String, String>>> getReviews() {
        List<Map<String, String>> reviews = new ArrayList<>();
        reviews.add(Map.of("name", "쥬안1", "description", "맛있었어요!", "image", "/default-image.png"));
        return ResponseEntity.ok(reviews);
    }

}
