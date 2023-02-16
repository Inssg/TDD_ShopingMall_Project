package org.inssg.backend.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ItemServiceTest {

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
    void 상품리스트조회() {
        //상품조회
        List<ItemResponse> response = itemService.getList();
        //상품검증
        assertThat(response.get(0).getName()).isEqualTo("프로틴");
        assertThat(response.get(0).getImgPath()).isEqualTo("https://unsplash.com/2");
        assertThat(response.get(0).getPrice()).isEqualTo(50000);
        assertThat(response.get(1).getName()).isEqualTo("닭가슴살");
        assertThat(response.get(1).getImgPath()).isEqualTo("https://unsplash.com/1");
        assertThat(response.get(1).getPrice()).isEqualTo(30000);
    }


}
