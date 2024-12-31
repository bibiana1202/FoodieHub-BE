package com.cherrymango.foodiehub.controller;

import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.dto.AddStoreRequestDto;
import com.cherrymango.foodiehub.dto.StoreDetailResponseDto;
import com.cherrymango.foodiehub.dto.UpdateStoreDetailDto;
import com.cherrymango.foodiehub.dto.UpdateStoreRequestDto;
import com.cherrymango.foodiehub.file.FileStore;
import com.cherrymango.foodiehub.repository.SiteUserRepository;
import com.cherrymango.foodiehub.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.security.Principal;

@Controller
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    final private StoreService storeService;
    final private SiteUserRepository siteUserRepository;
    final private FileStore fileStore;

    @ResponseBody
    @GetMapping("/store-image/{filename}")
    public Resource downloadStoreImage(@PathVariable("filename") String filename) throws MalformedURLException {
        System.out.println("fileStore.getFullPath(filename)");
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/save/{userId}")
    public String getSaveStore(@PathVariable("userId") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "store/register";
    }

    @PostMapping("/save/{userId}")
    public String postSaveStore(@ModelAttribute @Valid AddStoreRequestDto addStoreRequestDto, @PathVariable("userId") Long userId) {
        storeService.register(userId, addStoreRequestDto);
        return "redirect:/";
    }

    @GetMapping("/update/{storeId}")
    public String getUpdateStore(@PathVariable("storeId") Long storeId, Model model) {
        UpdateStoreDetailDto store = storeService.getUpdateDetails(storeId);
        model.addAttribute("store", store);
        return "store/update";
    }

    @PostMapping("/update/{storeId}")
    public String postUpdateStore(@ModelAttribute @Valid UpdateStoreRequestDto updateStoreRequestDto, @PathVariable("storeId") Long storeId) {
        storeService.update(storeId, updateStoreRequestDto);
        return "redirect:/store/update/" + storeId;
    }

    @GetMapping("/detail/{storeId}")
    public String getStoreDetails(@PathVariable("storeId") Long storeId, Principal principal, Model model) {

        // 추후 변경
        Long userId = null;
        if (principal != null) {
            String email = principal.getName(); // 사용자명 가져오기
            SiteUser user = siteUserRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            userId = user.getId(); // 사용자 ID 가져오기
        }

        StoreDetailResponseDto storeDetails = storeService.getStoreDetails(storeId, userId);
        model.addAttribute("store", storeDetails);

        return "store/detail";
    }

    // test mapping
    @GetMapping("/review/update")
    public String review() {
        return "review/update";
    }
}
