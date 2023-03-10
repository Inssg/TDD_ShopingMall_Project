package org.inssg.backend.cart;

import org.inssg.backend.item.Item;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.userdetails.MemberDetails;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;


    MemberDetails memberDetails;

    @BeforeAll
    public void setup() {
        String email = "abc@gmail.com";
        String password = "1234";
        String username = "테스트";

        MemberCreate memberCreate = MemberCreate.builder().username(username).password(password).email(email).build();
        Member member = Member.create(memberCreate, passwordEncoder);
        memberRepository.save(member);
        memberDetails = MemberDetails.of(member);
    }


    @Test
    @DisplayName("장바구니 추가 성공")
    void 장바구니_추가_성공() throws Exception {
        //given
        Long itemId = 1L;
        given(cartService.addCartItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt()))
                .willReturn(Cart.builder().itemId(itemId).memberId(1L).quantity(3).build());

        mockMvc.perform(post("/cart/items/{itemId}", itemId)
                        .param("quantity", "3")
                        .with(user(memberDetails))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(itemId))
                .andExpect(jsonPath("$.quantity").value(3));

    }

    @Test
    @DisplayName("장바구니 조회")
    void 장바구니_조회() throws Exception {
        //given
        List<Item> items = List.of(Item.create("닭가슴살", "http://unplash.com", 4000),
                                   Item.create("프로틴", "http://unpash2.com", 50000));
        List<Cart> carts = List.of(Cart.builder().itemId(1L).memberId(1L).quantity(3).build(),
                                    Cart.builder().itemId(2L).memberId(1L).quantity(5).build());
        List<CartResponse> cartResponses = List.of(CartResponse.of(carts.get(0), items.get(0)),
                CartResponse.of(carts.get(1), items.get(1)));

        given(cartService.getCartItems(Mockito.anyLong()))
                .willReturn(cartResponses);

        mockMvc.perform(get("/cart/items")
                        .with(user(memberDetails))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].item.name").value("닭가슴살"))
                .andExpect(jsonPath("$.[0].item.price").value(4000))
                .andExpect(jsonPath("$.[0].quantity").value(3))
                .andExpect(jsonPath("$.[1].item.name").value("프로틴"))
                .andExpect(jsonPath("$.[1].item.price").value(50000))
                .andExpect(jsonPath("$.[1].quantity").value(5));
    }

    @Test


}
