package com.example.nebula.dto.cart;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long itemId;
    private String productId;
    private String productName;
    private Integer price;
    private Integer quantity;
    private String imageUrl;

    public CartItemResponse(Long itemId, String productId, String productName, Integer price, Integer quantity, String imageUrl) {
        this.itemId = itemId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }
}