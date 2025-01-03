package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.domain.Menu;
import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.dto.AddMenuRequestDto;
import com.cherrymango.foodiehub.dto.MenuResponseDto;
import com.cherrymango.foodiehub.dto.UpdateMenuRequestDto;
import com.cherrymango.foodiehub.repository.StoreRepository;
import com.cherrymango.foodiehub.service.MenuService;
import com.cherrymango.foodiehub.service.StoreService;
import com.cherrymango.foodiehub.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class MenuApiController {
    private final MenuService menuService;
    private final StoreRepository storeRepository;
    final private StoreService storeService;
    private final TokenUtil tokenUtil;

//    @PostMapping("/menu/{storeId}")
//    public ResponseEntity<MenuResponseDto> addMenu(@PathVariable("storeId") Long storeId, @RequestBody @Valid AddMenuRequestDto request) {
//        Long id = menuService.save(storeId, request);
//        Menu findMenu = menuService.findOne(id);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new MenuResponseDto(findMenu.getId(), findMenu.getName(), findMenu.getPrice()));
//    }

//    @GetMapping("/menu/{storeId}")
//    public ResponseEntity<List<MenuResponseDto>> findAllMenus(@PathVariable("storeId") Long storeId) {
//        Store store = storeRepository.findById(storeId).get();
//        List<MenuResponseDto> menus = menuService.findMenus(store).stream()
//                .map(menu -> new MenuResponseDto(menu.getId(), menu.getName(), menu.getPrice()))
//                .toList();
//        return ResponseEntity.ok()
//                .body(menus);
//    }

//    @PutMapping("/menu")
//    public ResponseEntity<MenuResponseDto> updateMenu(@RequestBody @Valid UpdateMenuRequestDto request) {
//        Long id = menuService.update(request);
//        Menu findMenu = menuService.findOne(id);
//        return ResponseEntity.ok()
//                .body(new MenuResponseDto(findMenu.getId(), findMenu.getName(), findMenu.getPrice()));
//    }

    @DeleteMapping("/menu/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable("id") Long id) {
        menuService.delete(id);
        return ResponseEntity.ok().build();
    }


    // 식당 수정 (메뉴 로드) GET (박혜정 2025-01-03)
    @GetMapping("/menu")
    public ResponseEntity<List<MenuResponseDto>> findAllMenus(Principal principal, HttpServletRequest request) {
        Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
        // userId로 storeId 찾기
        Long storeId = storeService.getStoreIdByUserId(userId);


        Store store = storeRepository.findById(storeId).get();
        List<MenuResponseDto> menus = menuService.findMenus(store).stream()
                .map(menu -> new MenuResponseDto(menu.getId(), menu.getName(), menu.getPrice()))
                .toList();
        return ResponseEntity.ok()
                .body(menus);
    }

    // 식당 수정 (메뉴 등록) POST (박혜정 2025-01-03)
    @PostMapping("/menu")
    public ResponseEntity<MenuResponseDto> addMenu(@RequestBody @Valid AddMenuRequestDto addMenuRequestDto,Principal principal, HttpServletRequest request) {
        Long userId = tokenUtil.getSiteUserIdOrThrow(principal, request);
        // userId로 storeId 찾기
        Long storeId = storeService.getStoreIdByUserId(userId);

        Long id = menuService.save(storeId, addMenuRequestDto);
        Menu findMenu = menuService.findOne(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MenuResponseDto(findMenu.getId(), findMenu.getName(), findMenu.getPrice()));
    }

    // 식당 수정 (메뉴 수정) PUT (박혜정 2025-01-03)
    @PutMapping("/menu")
    public ResponseEntity<MenuResponseDto> updateMenu(@RequestBody @Valid UpdateMenuRequestDto request) {
        Long id = menuService.update(request);
        Menu findMenu = menuService.findOne(id);
        return ResponseEntity.ok()
                .body(new MenuResponseDto(findMenu.getId(), findMenu.getName(), findMenu.getPrice()));
    }

}
