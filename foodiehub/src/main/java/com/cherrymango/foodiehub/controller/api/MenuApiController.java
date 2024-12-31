package com.cherrymango.foodiehub.controller.api;

import com.cherrymango.foodiehub.domain.Menu;
import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.dto.AddMenuRequestDto;
import com.cherrymango.foodiehub.dto.MenuResponseDto;
import com.cherrymango.foodiehub.dto.UpdateMenuRequestDto;
import com.cherrymango.foodiehub.repository.StoreRepository;
import com.cherrymango.foodiehub.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class MenuApiController {
    private final MenuService menuService;
    private final StoreRepository storeRepository;

    @PostMapping("/menu/{storeId}")
    public ResponseEntity<MenuResponseDto> addMenu(@PathVariable("storeId") Long storeId, @RequestBody @Valid AddMenuRequestDto request) {
        Long id = menuService.save(storeId, request);
        Menu findMenu = menuService.findOne(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MenuResponseDto(findMenu.getId(), findMenu.getName(), findMenu.getPrice()));
    }

    @GetMapping("/menu/{storeId}")
    public ResponseEntity<List<MenuResponseDto>> findAllMenus(@PathVariable("storeId") Long storeId) {
        Store store = storeRepository.findById(storeId).get();
        List<MenuResponseDto> menus = menuService.findMenus(store).stream()
                .map(menu -> new MenuResponseDto(menu.getId(), menu.getName(), menu.getPrice()))
                .toList();
        return ResponseEntity.ok()
                .body(menus);
    }

    @PutMapping("/menu")
    public ResponseEntity<MenuResponseDto> updateMenu(@RequestBody @Valid UpdateMenuRequestDto request) {
        Long id = menuService.update(request);
        Menu findMenu = menuService.findOne(id);
        return ResponseEntity.ok()
                .body(new MenuResponseDto(findMenu.getId(), findMenu.getName(), findMenu.getPrice()));
    }

    @DeleteMapping("/menu/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable("id") Long id) {
        menuService.delete(id);
        return ResponseEntity.ok().build();
    }

}
