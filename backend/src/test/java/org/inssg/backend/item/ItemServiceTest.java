package org.inssg.backend.item;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import javax.swing.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemServiceTest {


    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemService();
    }

    //TODO상품조회리스트 테스트 구현
    @Test
    void 상품조회() {
        //상품조회
        ItemResponse response = itemService.getList();
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

        public ItemResponse getList() {
            return null;
        }
    }
}
