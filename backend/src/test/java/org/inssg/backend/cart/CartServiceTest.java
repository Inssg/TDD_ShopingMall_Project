package org.inssg.backend.cart;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.inssg.backend.item.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CartServiceTest {

    private CartService cartService;

    Item 프로틴;
    Item 닭가슴살;
    private CartRepository cartRepository;
    String email;

    @BeforeEach
    void setup() {
        프로틴 = Item.create("프로틴", "https://unsplash.com/2", 50000);
        닭가슴살 = Item.create("닭가슴살", "https://unsplash.com/1", 30000);
        email = "abc@gmail.com";
        cartRepository = new CartRepository();
        cartService = new CartService();
    }

    @Test
    void test_addCartItem() {
        Cart cart = cartService.addCartItem(프로틴, 1L);

        assertThat(cart.items.get(0).getName()).isEqualTo("프로틴");
        assertThat(cart.quantity).isEqualTo(1);
    }

    private class CartService {
        public Cart addCartItem(Item item, Long memberId) {
            Cart cart = cartRepository.findByMemberId(memberId);
            cart.addItem(item);

            return cart;
        }
    }

    private class CartRepository {
        public Cart findByMemberId(Long memberId) {
            Cart cart = new Cart(1L, new ArrayList<Item>(),1L,1);
            return cart;
        }
    }

    private class Cart {
        private Long Id;
        private List<Item> items;
        private Long memberId;
        private int quantity;

        public Cart(Long id, List<Item> items, Long memberId, int quantity) {
            Id = id;
            this.items = items;
            this.memberId = memberId;
            this.quantity = quantity;
        }

        public void addItem(Item item) {
            this.items.add(item);
        }
    }
}
