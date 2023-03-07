package org.inssg.backend.cart;

import org.inssg.backend.item.Item;
import org.inssg.backend.item.ItemRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartServiceTest {

    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ItemRepository itemRepository;

    Item 프로틴;
    Item 닭가슴살;
    Item 샐러드;
    Item 스트랩;

    @BeforeAll
    void setup() {
        프로틴 = Item.create("프로틴", "https://unsplash.com/2", 50000);
        닭가슴살 = Item.create("닭가슴살", "https://unsplash.com/1", 30000);
        샐러드 = Item.create("샐러드", "https://unsplash.com/1", 3000);
        스트랩 = Item.create("스트랩", "https://unsplash.com/1", 15000);
        itemRepository.saveAll(List.of(프로틴, 닭가슴살, 샐러드, 스트랩));
    }

    @Test
    void test_addCartItem() {
        Cart cart = cartService.addCartItem(3L, 1L, 3);

        assertThat(itemRepository.findById(cart.getItemId()).get().getName()).isEqualTo("샐러드");
        assertThat(cart.getQuantity()).isEqualTo(3);
    }

    @Test
    void test_getCartItems() {
        //given
        cartService.addCartItem(1L, 1L, 2);
        cartService.addCartItem(2L, 1L, 3);

        //when
        List<Item> cartItems = cartService.getCartItems(1L);

        assertThat(cartItems.get(0).getName()).isEqualTo("프로틴");
        assertThat(cartItems.get(1).getName()).isEqualTo("닭가슴살");
    }

    @Test
    void test_removeCartItem() {
        //given
        Cart cart = cartService.addCartItem(4L, 1L, 3);

        //when
        cartService.removeCartItem(1L, 4L);
        assertThat(cartRepository.findByMemberIdAndItemId(1L,4L)).isNull();
    }

}
