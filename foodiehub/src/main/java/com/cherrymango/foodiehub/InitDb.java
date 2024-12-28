package com.cherrymango.foodiehub;

import com.cherrymango.foodiehub.domain.*;
import com.cherrymango.foodiehub.repository.SiteUserRepository;
import com.cherrymango.foodiehub.repository.TagRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb { // 카테고리, 태그

    private final InitService initService;

    @PostConstruct
    private void init() {
//        initService.dbInit1();
//        initService.dbInit2();
//        initService.dbInit3();
//        initService.dbInit4();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        private final SiteUserRepository siteUserRepository;
        private final TagRepository tagRepository;

        public void dbInit1() {
            SiteUser user = new SiteUser();
            user.setEmail("thswjd0609@gmail.com");
            user.setPassword("1234");
            em.persist(user);
        }

        public void dbInit2() { // 카테고리
            createTag("혼밥");
            createTag("모임");
            createTag("회식");
            createTag("데이트");
            createTag("감성");
            createTag("노포");
            createTag("부모님");
        }

        public void createTag(String name) {
            Tag tag = new Tag();
            tag.setName(name);
            em.persist(tag);
        }

        public void dbInit3() {
            SiteUser siteUser = siteUserRepository.findByEmail("thswjd0609@gmail.com").get();

            Store store = new Store();
            store.setUser(siteUser);
            store.setName("청마루");
            store.setIntro("육회비빔밥 맛집");
            store.setPhone("010-2093-6947");
            store.setAddress("서울시 용두동");
            store.setCategory(Category.KOREAN);
            store.setParking(0);
            store.setContent("청마루 가게 소개");
            store.addStoreTag(StoreTag.createStoreTag(tagRepository.findByName("혼밥").get()));
            store.addStoreTag(StoreTag.createStoreTag(tagRepository.findByName("부모님").get()));
            store.addMenu(Menu.createMenu("육회비빔밥", 13000));
            store.addMenu(Menu.createMenu("된장찌개", 8000));

            em.persist(store);
        }

        public void dbInit4() {
            SiteUser user = new SiteUser();
            user.setEmail("aksen0609@naver.com");
            user.setPassword("123");
            em.persist(user);
        }

    }
}