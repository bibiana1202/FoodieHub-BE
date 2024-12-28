package com.cherrymango.foodiehub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer price;

    //==생성 메소드==//
    public static Menu createMenu(String name, Integer price) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);

        return menu;
    }

    public static Menu createMenu(String name, Integer price, Store store) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setStore(store);

        return menu;
    }
}
