package org.inssg.backend.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCreate {

    @NotBlank
    private String userName;
    @NotBlank
    private String address;
    @NotNull
    private Long itemId;
    @NotNull
    private Integer quantity;

    @Builder
    public OrderCreate(String userName, String address, Long itemId, int quantity) {
        this.userName = userName;
        this.address = address;
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
