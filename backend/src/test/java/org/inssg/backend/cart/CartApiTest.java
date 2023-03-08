package org.inssg.backend.cart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class CartApiTest {

    @Autowired
    private MockMvc mockMvc;



    @Test
    @DisplayName("장바구니 추가 성공")
    void 장바구니추가_성공() {
        mockMvc.perform(post("/cart/items/{itemId}"))

    }


}
