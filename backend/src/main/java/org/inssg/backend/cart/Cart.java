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
    private Long Id;

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
