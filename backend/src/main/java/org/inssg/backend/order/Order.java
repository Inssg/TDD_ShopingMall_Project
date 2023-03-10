package org.inssg.backend.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name ="Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="order_id")
    private Long id;

    private Long memberId;

    @Column(length = 50, nullable = false)
    private String userName;

    @Column(length = 500, nullable = false)
    private String address;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private int quantity;

    @Builder
    public Order(Long memberId, String userName, String address, Long itemId, int quantity) {
        this.memberId = memberId;
        this.userName = userName;
        this.address = address;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public static Order create(Long memberId, OrderCreate orderCreate) {
        return Order.builder()
                .memberId(memberId)
                .userName(orderCreate.getUserName())
                .address(orderCreate.getAddress())
                .itemId(orderCreate.getItemId())
                .quantity(orderCreate.getQuantity())
                .build();
    }


}
