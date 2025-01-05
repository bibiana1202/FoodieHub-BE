package com.cherrymango.foodiehub.service;


import com.cherrymango.foodiehub.domain.*;
import com.cherrymango.foodiehub.dto.*;
import com.cherrymango.foodiehub.file.FileStore;
import com.cherrymango.foodiehub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final SiteUserRepository siteUserRepository;
    private final TagRepository tagRepository;
    private final FileStore fileStore;
    private final StoreLikeRepository storeLikeRepository;
    private final StoreFavoriteRepository storeFavoriteRepository;

    @Transactional
    public Long register(Long userId, AddStoreRequestDto addStoreRequestDto) {
        SiteUser user = siteUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<Menu> menus = Optional.ofNullable(addStoreRequestDto.getMenus())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(menu -> Menu.createMenu(menu.getName(), menu.getPrice()))
                .toList();

        List<StoreTag> storeTags = Optional.ofNullable(addStoreRequestDto.getTags())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseThrow(() -> new IllegalArgumentException("Tag not found with name: " + tagName)))
                .map(StoreTag::createStoreTag)
                .toList();

        List<StoreImage> storeImages = addStoreRequestDto.getImages().stream()
                .filter(image -> !image.isEmpty())
                .map(fileStore::storeFile)
                .map(uploadImageDto -> StoreImage.createStoreImage(uploadImageDto.getUploadFileName(), uploadImageDto.getStoreFileName()))
                .toList();

        Store store = Store.createStore(
                user,
                addStoreRequestDto.getName(),
                addStoreRequestDto.getIntro(),
                addStoreRequestDto.getPhone(),
                addStoreRequestDto.getAddress(),
                addStoreRequestDto.getCategory(),
                addStoreRequestDto.getParking(),
                addStoreRequestDto.getOperationHours(),
                addStoreRequestDto.getLastOrder(),
                addStoreRequestDto.getContent(),
                menus,
                storeTags,
                storeImages
        );

        storeRepository.save(store);

        return store.getId();
    }

    public UpdateStoreDetailResponseDto getUpdateDetails(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + id));

        List<String> tags = store.getStoreTags().stream()
                .map(storeTag -> storeTag.getTag().getName())
                .toList();

        return UpdateStoreDetailResponseDto.builder()
                .id(store.getId())
                .name(store.getName())
                .intro(store.getIntro())
                .phone(store.getPhone())
                .address(store.getAddress())
                .category(store.getCategory())
                .parking(store.getParking())
                .operationHours(store.getOperationHours())
                .lastOrder(store.getLastOrder())
                .content(store.getContent())
                .registerDate(store.getRegisterDate())
                .tags(tags)
                .build();
    }

    @Transactional
    public void update(Long id, UpdateStoreRequestDto updateStoreRequestDto) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + id));
        store.setName(updateStoreRequestDto.getName());
        store.setIntro(updateStoreRequestDto.getIntro());
        store.setPhone(updateStoreRequestDto.getPhone());
        store.setAddress(updateStoreRequestDto.getAddress());
        store.setCategory(updateStoreRequestDto.getCategory());
        store.setParking(updateStoreRequestDto.getParking());
        store.setOperationHours(updateStoreRequestDto.getOperationHours());
        store.setLastOrder(updateStoreRequestDto.getLastOrder());
        store.setContent(updateStoreRequestDto.getContent());
        updateTags(store, updateStoreRequestDto.getTags());
    }

    private void updateTags(Store store, List<String> tags) {
        List<String> safeTags = tags != null ? tags : Collections.emptyList(); // null 방지

        // 기존 태그 이름 리스트
        Set<String> existingTagNames = store.getStoreTags().stream()
                .map(storeTag -> storeTag.getTag().getName())
                .collect(Collectors.toSet());

        // 추가할 태그 식별
        List<Tag> tagsToAdd = safeTags.stream()
                .filter(tagName -> !existingTagNames.contains(tagName)) // 기존 태그에 없는 태그 필터링
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.createTag(tagName)))) // 없는 태그는 생성
                .toList();

        // 삭제할 태그 식별 (새 태그에 없는 기존 태그)
        List<StoreTag> storeTagsToRemove = store.getStoreTags().stream()
                .filter(storeTag -> !safeTags.contains(storeTag.getTag().getName())) // 새 태그 리스트에 없는 기존 태그 필터링
                .toList();

        // StoreTag 추가
        for (Tag tag : tagsToAdd) {
            store.addStoreTag(StoreTag.createStoreTag(tag));
        }

        // StoreTag 삭제
        for (StoreTag storeTag : storeTagsToRemove) {
            store.getStoreTags().remove(storeTag);
        }
    }

    // 가게 상세 페이지 정보 제공
    public StoreDetailResponseDto getStoreDetails(Long id, Long userId) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with id: " + id));

        SiteUser user = (userId != null)
                ? siteUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"))
                : null;

        Boolean isLiked = user != null && storeLikeRepository.existsByStoreAndUser(store, user);
        Boolean isFavorite = user != null && storeFavoriteRepository.existsByStoreAndUser(store, user);

        List<String> tags = extractTags(store.getStoreTags());
        List<String> imageNames = extractImageNames(store.getStoreImageList());
        Double avgRating = roundToFirstDecimal(calculateAverageRating(store, Review::getAvgRating));

        return mapToStoreDetailResponseDto(store, tags, imageNames, avgRating, isLiked, isFavorite);
    }

    private List<String> extractTags(List<StoreTag> storeTags) {
        return storeTags.stream()
                .map(storeTag -> storeTag.getTag().getName())
                .toList();
    }

    private List<String> extractImageNames(List<StoreImage> storeImages) {
        return storeImages.stream()
                .map(StoreImage::getStoreImageName)
                .toList();
    }

    private Double calculateAverageRating(Store store, ToDoubleFunction<Review> ratingExtractor) {
        return store.getReviews().stream()
                .mapToDouble(ratingExtractor)
                .average()
                .orElse(0.0);
    }

    private Double roundToFirstDecimal(Double value) {
        return value != null ? Math.round(value * 10) / 10.0 : null;
    }

    private StoreDetailResponseDto mapToStoreDetailResponseDto(Store store, List<String> tags, List<String> imageNames,
                                                               Double avgRating, Boolean isLiked, Boolean isFavorite) {
        return StoreDetailResponseDto.builder()
                .id(store.getId())
                .name(store.getName())
                .intro(store.getIntro())
                .phone(store.getPhone())
                .address(store.getAddress())
                .category(store.getCategory().getDescription())
                .parking(store.getParking())
                .operationHours(store.getOperationHours())
                .lastOrder(store.getLastOrder())
                .content(store.getContent())
                .registerDate(store.getRegisterDate())
                .menus(store.getMenus().stream()
                        .map(menu -> new MenuResponseDto(null, menu.getName(), menu.getPrice()))
                        .toList())
                .images(imageNames)
                .tags(tags)
                .avgRating(avgRating)
                .likes(store.getStoreLikes().size())
                .favorites(store.getStoreFavorites().size())
                .isLiked(isLiked)
                .isFavorite(isFavorite)
                .build();
    }

    public List<MyPageStoreResponseDto> getStoreLikeList(Long userId) {

        SiteUser user = siteUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<StoreLike> storeLikes = storeLikeRepository.findByUserOrderByLikeTimeDesc(user); // 최신순으로 정렬된 StoreLike 리스트

        return storeLikes.stream()
                .map(storeLike -> {
                    Store store = storeLike.getStore();

                    String imagePath = store.getStoreImageList() != null && !store.getStoreImageList().isEmpty()
                            ? store.getStoreImageList().get(0).getStoreImageName()
                            : null; // 첫 번째 이미지 사용

                    return MyPageStoreResponseDto.builder()
                            .id(store.getId())
                            .name(store.getName())
                            .intro(store.getIntro())
                            .category(store.getCategory().getDescription())
                            .content(store.getContent())
                            .image(imagePath)
                            .build();
                })
                .collect(Collectors.toList());

    }

    public List<MyPageStoreResponseDto> getStoreFavoriteList(Long userId) {

        SiteUser user = siteUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<StoreFavorite> storeFavorites = storeFavoriteRepository.findByUserOrderByFavoriteTimeDesc(user); // 최신순으로 정렬된 StoreFavorite 리스트

        return storeFavorites.stream()
                .map(storeFavorite -> {
                    Store store = storeFavorite.getStore();

                    String imagePath = store.getStoreImageList() != null && !store.getStoreImageList().isEmpty()
                            ? store.getStoreImageList().get(0).getStoreImageName()
                            : null; // 첫 번째 이미지 사용

                    return MyPageStoreResponseDto.builder()
                            .id(store.getId())
                            .name(store.getName())
                            .intro(store.getIntro())
                            .category(store.getCategory().getDescription())
                            .content(store.getContent())
                            .image(imagePath)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public MyStoreResponseDto getMyStoreDetails(Long userId) {
        SiteUser user = siteUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Store> storeOptional = storeRepository.findByUser(user);
        if (storeOptional.isEmpty()) {
            return null; // Store 없을 시 null 반환
        }

        Store store = storeOptional.get();

        Double averageTotalRating = roundToFirstDecimal(calculateAverageRating(store, Review::getAvgRating));
        Double averageTasteRating = roundToFirstDecimal(calculateAverageRating(store, Review::getTasteRating));
        Double averagePriceRating = roundToFirstDecimal(calculateAverageRating(store, Review::getPriceRating));
        Double averageCleanRating = roundToFirstDecimal(calculateAverageRating(store, Review::getCleanRating));
        Double averageFriendlyRating = roundToFirstDecimal(calculateAverageRating(store, Review::getFriendlyRating));

        return MyStoreResponseDto.builder()
                .id(store.getId())
                .name(store.getName())
                .averageTotalRating(averageTotalRating)
                .averageTasteRating(averageTasteRating)
                .averagePriceRating(averagePriceRating)
                .averageCleanRating(averageCleanRating)
                .averageFriendlyRating(averageFriendlyRating)
                .build();
    }

    public List<StoreListItemResponseDto> getStoresByCategory(Category category, int limit) {
        // Pageable 생성: limit이 0이면 Pageable.unpaged(), 그렇지 않으면 PageRequest 생성
        Pageable pageable = limit > 0 ? PageRequest.of(0, limit) : Pageable.unpaged();
        List<Store> stores = storeRepository.findByCategoryOrderByRegisterDateDesc(category, pageable);

        return stores.stream()
                .map(store -> StoreListItemResponseDto.builder()
                        .id(store.getId())
                        .name(store.getName())
                        .intro(store.getIntro())
                        .content(store.getContent())
                        .image(store.getStoreImageList().isEmpty() ? null : store.getStoreImageList().get(0).getStoreImageName()) // 첫 번째 이미지 URL 사용
                        .avgRating(roundToFirstDecimal(calculateAverageRating(store, Review::getAvgRating))) // 평균 평점 계산 후 반올림
                        .build())
                .collect(Collectors.toList());
    }

    public List<StoreListItemResponseDto> getStoresByTag(String tagName, int limit) {
        Pageable pageable = limit > 0 ? PageRequest.of(0, limit) : Pageable.unpaged();
        List<Store> stores = storeRepository.findByTagNameOrderByRegisterDateDesc(tagName, pageable);

        return stores.stream()
                .map(store -> StoreListItemResponseDto.builder()
                        .id(store.getId())
                        .name(store.getName())
                        .intro(store.getIntro())
                        .content(store.getContent())
                        .image(store.getStoreImageList().isEmpty() ? null : store.getStoreImageList().get(0).getStoreImageName())
                        .avgRating(roundToFirstDecimal(calculateAverageRating(store, Review::getAvgRating)))
                        .build())
                .collect(Collectors.toList());
    }

    public List<StoreListItemResponseDto> getAllStores(int limit) {
        Pageable pageable = limit > 0 ? PageRequest.of(0, limit) : Pageable.unpaged();
        List<Store> stores = storeRepository.findAllByOrderByRegisterDateDesc(pageable);

        return stores.stream()
                .map(store -> StoreListItemResponseDto.builder()
                        .id(store.getId())
                        .name(store.getName())
                        .intro(store.getIntro())
                        .content(store.getContent())
                        .image(store.getStoreImageList().isEmpty() ? null : store.getStoreImageList().get(0).getStoreImageName())
                        .avgRating(roundToFirstDecimal(calculateAverageRating(store, Review::getAvgRating)))
                        .build())
                .collect(Collectors.toList());
    }

    // 박혜정 2025-01-03
    public Long getStoreIdByUserId(Long userId) {
        SiteUser user = siteUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Store> storeOptional = storeRepository.findByUser(user);
        if (storeOptional.isEmpty()) {
            return null; // Store 없을 시 null 반환
        }

        Store store = storeOptional.get();
        return store.getId();
    }
}
