package org.inssg.backend.cart;

import lombok.RequiredArgsConstructor;
import org.inssg.backend.item.Item;
import org.inssg.backend.item.ItemNotFound;
import org.inssg.backend.item.ItemRepository;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    public Cart addCartItem(Long itemId, Long memberId, int quantity) {
        Cart cart = cartRepository.findByMemberIdAndItemId(memberId, itemId);

        if (cart == null) {
            Cart newCart = Cart.builder()
                    .itemId(itemId)
                    .memberId(memberId)
                    .quantity(quantity)
                    .build();
            cartRepository.save(newCart);
            return newCart;
        }
        cart.setQuantity(quantity);
        return cart;
    }

    public List<Item> getCartItems(Long memberId) {
        List<Cart> carts = cartRepository.findByMemberId(memberId);
        List<Long> itemIds = carts.stream().map(cart -> cart.getItemId()).collect(Collectors.toList());
        List<Item> items = itemIds.stream()
                .map(itemId -> itemRepository.findById(itemId).orElseThrow(ItemNotFound::new))
                .collect(Collectors.toList());
        return items;
    }

    public void removeCartItem(long memberId, long itemId) {
        Cart cart = cartRepository.findByMemberIdAndItemId(memberId, itemId);
        cartRepository.delete(cart);
    }
}
