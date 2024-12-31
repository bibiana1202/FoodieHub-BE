package com.cherrymango.foodiehub.domain;

public enum Category { // CuisineType

    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    ITALIAN("이탈리아"),
    FRENCH("프랑스"),
    ASIAN("아시아"),
    MEXICAN("멕시코"),
    OTHER("기타");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
