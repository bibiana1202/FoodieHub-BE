package com.cherrymango.foodiehub;

import com.cherrymango.foodiehub.domain.Tag;
import com.cherrymango.foodiehub.repository.SiteUserRepository;
import com.cherrymango.foodiehub.repository.TagRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    private void init() {
        //initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        private final SiteUserRepository siteUserRepository;
        private final TagRepository tagRepository;


        public void dbInit1() { // 카테고리
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

    }
}
