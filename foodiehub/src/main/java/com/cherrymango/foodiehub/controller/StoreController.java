package com.cherrymango.foodiehub.controller;

import com.cherrymango.foodiehub.dto.AddStoreRequestDto;
import com.cherrymango.foodiehub.dto.UpdateStoreDetailDto;
import com.cherrymango.foodiehub.dto.UpdateStoreRequestDto;
import com.cherrymango.foodiehub.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    final private StoreService storeService;

    @GetMapping("/save/{userId}")
    public String getSaveStore(@PathVariable("userId") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "store/register";
    }

    @PostMapping("/save/{userId}")
    public String saveStore(@ModelAttribute @Valid AddStoreRequestDto addStoreRequestDto, @PathVariable("userId") Long userId) {
        storeService.register(userId, addStoreRequestDto);
        return "redirect:/";
    }

    @GetMapping("/update/{storeId}")
    public String getUpdateStore(@PathVariable("storeId") Long storeId, Model model) {
        UpdateStoreDetailDto store = storeService.detail(storeId);
        model.addAttribute("store", store);
        model.addAttribute("storeId", storeId);
        return "store/update";
    }

    @PostMapping("/update/{storeId}")
    public String updateStore(@ModelAttribute @Valid UpdateStoreRequestDto updateStoreRequestDto, @PathVariable("storeId") Long storeId) {
        storeService.update(storeId, updateStoreRequestDto);
        return "redirect:/store/update/" + storeId;
    }
}
