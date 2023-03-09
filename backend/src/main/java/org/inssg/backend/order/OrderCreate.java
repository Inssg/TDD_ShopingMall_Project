package org.inssg.backend.order;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class OrderCreate {

    @NotBlank
    private String userName;
    @NotBlank
    private String address;
    @NotBlank
    private Long itemId;
    @NotBlank
    private int quantity;

    @Builder
    public OrderCreate(String userName, String address, Long itemId, int quantity) {
        this.userName = userName;
        this.address = address;
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
