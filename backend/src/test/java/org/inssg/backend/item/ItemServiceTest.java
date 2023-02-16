package org.inssg.backend.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.awt.*;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ItemServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        Item protein = Item.create("프로틴", "https://unsplash.com/2", 50000);
        Item chicken = Item.create("닭가슴살", "https://unsplash.com/1", 30000);
        itemRepository.save(protein);
        itemRepository.save(chicken);
    }

    //TODO 상품조회리스트 테스트 구현
    @Test
    void 상품리스트조회() throws Exception {

        //given

        //when
        List<ItemResponse> response = itemService.getItemList();

        ResultActions actions = mockMvc.perform(get("/items")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value("프로틴"))
                .andExpect(jsonPath("$.[0].price").value(50000))
                .andExpect(jsonPath("$.[1].name").value("닭가슴살"))
                .andExpect(jsonPath("$.[1].price").value(30000));
    }


}
