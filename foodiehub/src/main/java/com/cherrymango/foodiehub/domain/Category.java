package com.cherrymango.foodiehub.domain;

public enum Category { // CuisineType

    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    ITALIAN("이태리"),
    FRENCH("프렌치"),
    ASIAN("아시안"),
    MEXICAN("멕시칸"),
    OTHER("기타");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
