package org.inssg.backend.cart;

import org.inssg.backend.item.Item;
import org.inssg.backend.item.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CartServiceTest {

    private CartService cartService;
    private CartRepository cartRepository;
    private Map<Long,Item> itemRepository;


    Item 프로틴;
    Item 닭가슴살;
    String email;

    @BeforeEach
    void setup() {
        프로틴 = Item.create("프로틴", "https://unsplash.com/2", 50000);
        닭가슴살 = Item.create("닭가슴살", "https://unsplash.com/1", 30000);
        email = "abc@gmail.com";
        cartRepository = new CartRepository();
        cartService = new CartService();
        itemRepository = new HashMap<>();
        itemRepository.put(1L, 프로틴);
        itemRepository.put(2L, 닭가슴살);
    }

    @Test
    void test_addCartItem() {
        Cart cart = cartService.addCartItem(1L, 1L, 3);

        assertThat(itemRepository.get(cart.itemId).getName()).isEqualTo("프로틴");
        assertThat(cart.quantity).isEqualTo(3);
    }

    @Test
    void test_getCartItems() {
        List<Item> cartItems = cartService.getCartItems(1L);

        assertThat(cartItems.get(0).getName()).isEqualTo("프로틴");
        assertThat(cartItems.get(1).getName()).isEqualTo("닭가슴살");
    }

    @Test
    void test_removeCartItem() {

    }

    private class CartService {
        public Cart addCartItem(Long itemId, Long memberId, int quantity) {
            Cart cart = cartRepository.findByMemberIdAndItemId(memberId, itemId);

            if (cart == null) {
                Cart newCart = new Cart(1L, 1L, quantity);
                cartRepository.save(newCart);
                return newCart;
            }
            cart.setQuantity(quantity);
            return cart;
        }

        public List<Item> getCartItems(Long memberId) {
            List<Cart> carts = cartRepository.findByMemberId(memberId);
            List<Long> itemIds = carts.stream().map(cart -> cart.itemId).collect(Collectors.toList());
            List<Item> items = itemIds.stream().map(itemId -> itemRepository.get(itemId)).collect(Collectors.toList());
            return items;
        }
    }

    private class CartRepository {
        Map<Long, Cart> persistence = new HashMap<>();

        private Long sequence = 0L;

        public List<Cart> findByMemberId(Long memberId) {
            List<Cart> carts = List.of(new Cart(1L, 1L, 2),
                                    new Cart(2L, 1L, 3));
            return carts;
        }

        public Cart findByMemberIdAndItemId(Long memberId, Long itemId) {
            return null;
        }

        public void save(Cart cart) {
            cart.setId(++sequence);
            persistence.put(cart.Id, cart);
        }
    }

    private class Cart {
        private Long Id;
        private Long itemId;
        private Long memberId;
        private int quantity;

        public Cart(Long itemId, Long memberId, int quantity) {
            this.itemId = itemId;
            this.memberId = memberId;
            this.quantity = quantity;
        }

        public void setId(Long id) {
            Id = id;
        }

        public void setQuantity(int quantity) {
            this.quantity += quantity;
        }

    }
}
