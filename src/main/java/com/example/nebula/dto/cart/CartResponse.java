package com.example.nebula.dto.cart;

import lombok.Data;
import java.util.List;

@Data
public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private Integer subtotal;
    private Integer total;

    public CartResponse(Long cartId, List<CartItemResponse> items, Integer subtotal, Integer total) {
        this.cartId = cartId;
        this.items = items;
        this.subtotal = subtotal;
        this.total = total;
    }
}