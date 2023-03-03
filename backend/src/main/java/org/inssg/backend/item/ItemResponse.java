package org.inssg.backend.item;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ItemResponse {
    private Long id;
    private String name;
    private String imgPath;
    private int price;

    @Builder
    public ItemResponse(Long id, String name, String imgPath, int price) {
        this.id = id;
        this.name = name;
        this.imgPath = imgPath;
        this.price = price;
    }

    public static ItemResponse toResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .imgPath(item.getImgPath())
                .price(item.getPrice())
                .build();
    }
}
