package com.example.nebula.dto.product;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;
    private String category;

    public ProductResponse(Long id, String name, String description, Integer price, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }
}