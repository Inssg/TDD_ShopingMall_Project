package org.inssg.backend.item;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import javax.swing.*;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemServiceTest {


    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemService();
    }

    //TODO 상품조회리스트 테스트 구현
    @Test
    void 상품리스트조회() {
        //상품조회
        List<ItemResponse> response = itemService.getList();
        //상품검증
        assertThat(response).isNotNull();
    }

    private class ItemResponse {
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
    }

    private class ItemService {
        //TODO: ItemRepository에서 item List 뽑아와서 ItemResponse List로 변경
        //TODO: ItemRepository findAll -> findList (페이지네이션해서 조회로 변경필요)
        public List<ItemResponse> getList() {
            List<Item> item = itemRepository.findAll();

            return List.of(
                   new ItemResponse(1L,"프로틴","https://unsplash.com/2",50000),
                   new ItemResponse(2L,"닭가슴살","https://unsplash.com/1",30000),
                   new ItemResponse(3L,"밀크시슬","https://unsplash.com/3",20000)
            );
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
}
