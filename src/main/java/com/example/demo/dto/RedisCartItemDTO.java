package com.example.demo.dto;

public class RedisCartItemDTO {

    private Long productId;
    private int quantity;

    // 기본 생성자
    public RedisCartItemDTO() {}

    // 생성자
    public RedisCartItemDTO(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getter 및 Setter
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
