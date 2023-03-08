package org.inssg.backend.cart;

import lombok.Builder;
import lombok.Getter;
import org.inssg.backend.item.Item;
import org.inssg.backend.item.ItemResponse;

import java.util.List;

@Getter
public class CartResponse {

    private Long cartId;
    private Item item;
    private int quantity;

    @Builder
    public CartResponse(Long cartId, Item item, int quantity) {
        this.cartId = cartId;
        this.item = item;
        this.quantity = quantity;
    }

    public static CartResponse of(Cart cart,Item item) {

        return CartResponse.builder()
                .cartId(cart.getId())
                .item(item)
                .quantity(cart.getQuantity())
                .build();
    }

}
