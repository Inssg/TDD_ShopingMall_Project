package org.inssg.backend.cart;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Todo: Cart_Item 다대다로 변경하는것 고려해보기, 지금은 간접 참조중
    private Long itemId;

    private Long memberId;

    private int quantity;

    @Builder
    public Cart(Long itemId, Long memberId, int quantity) {
        this.itemId = itemId;
        this.memberId = memberId;
        this.quantity = quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity += quantity;
    }

}
