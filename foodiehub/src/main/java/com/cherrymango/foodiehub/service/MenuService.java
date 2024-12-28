package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.domain.Menu;
import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.dto.AddMenuRequestDto;
import com.cherrymango.foodiehub.dto.UpdateMenuRequestDto;
import com.cherrymango.foodiehub.repository.MenuRepository;
import com.cherrymango.foodiehub.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public Long save(Long storeId, AddMenuRequestDto request) {
        Store store = storeRepository.findById(storeId).get();
        Menu menu = Menu.createMenu(request.getName(), request.getPrice(), store);
        menuRepository.save(menu);
        return menu.getId();
    }

    public Menu findOne(Long id) {
        return menuRepository.findById(id).get();
    }

    public List<Menu> findMenus(Store store) {
        return menuRepository.findByStore(store);
    }

    @Transactional
    public Long update(UpdateMenuRequestDto request) {
        Menu menu = menuRepository.findById(request.getId()).get();
        menu.setName(request.getName());
        menu.setPrice(request.getPrice());
        System.out.println("request = " + request);
        System.out.println("menu = " + menu);
        return menu.getId();
    }

    @Transactional
    public void delete(Long id) {
        menuRepository.deleteById(id);
    }

}
