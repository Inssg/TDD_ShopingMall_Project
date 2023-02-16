package org.inssg.backend.item;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import javax.swing.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemServiceTest {


    private ItemService itemService;
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemService = new ItemService();
        itemRepository = new ItemRepository();
    }

    //TODO 상품조회리스트 테스트 구현
    @Test
    void 상품리스트조회() {
        //상품조회
        List<ItemResponse> response = itemService.getList();
        //상품검증
        assertThat(response).isNotNull();
    }

    public static class ItemResponse {
        Long id;
        String name;
        String imgPath;
        int price;

        public ItemResponse(Long id, String name, String imgPath, int price) {
            this.id = id;
            this.name = name;
            this.imgPath = imgPath;
            this.price = price;
        }

        public static ItemResponse toResponse(Item item) {
            return new ItemResponse(item.id, item.name, item.imgPath, item.price);
        }
    }

    private class ItemService {
        //TODO: ItemRepository에서 item List 뽑아와서 ItemResponse List로 변경
        //TODO: ItemRepository findAll -> findList (페이지네이션해서 조회로 변경필요)
        public List<ItemResponse> getList() {
            List<Item> items = itemRepository.findAll();

            List<ItemResponse> response = items.stream()
                    .map(item -> ItemResponse.toResponse(item))
                    .collect(Collectors.toList());
            return response;
        }
    }

    private class Item {
        private Long id;
        private String name;
        private String imgPath;
        private int price;

        public Item(Long id, String name, String imgPath, int price) {
            this.id = id;
            this.name = name;
            this.imgPath = imgPath;
            this.price = price;
        }
    }
    //TODO: 실제 DB에서 조회한 ITEM 반환하도록 변경
    private class ItemRepository {
        private Long sequence = 0L;
        private Map<Long, Item> persistence = new HashMap<>();

        public List<Item> findAll() {
            return List.of(
                    new Item(1L,"프로틴","https://unsplash.com/2",50000),
                    new Item(2L,"닭가슴살","https://unsplash.com/1",30000),
                    new Item(3L,"밀크시슬","https://unsplash.com/3",20000)
            );
        }
    }
}
