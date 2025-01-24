package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CartItemDTO {
    private String productName;
    private double productPrice;
    private int quantity;
    private double totalPrice;
    private Long productId;
    private String userId;

    // 기본 생성자 (Jackson 역직렬화용)
    public CartItemDTO() {}

    // 전체 필드를 초기화하는 생성자
    @JsonCreator
    public CartItemDTO(
            @JsonProperty("productName") String productName,
            @JsonProperty("productPrice") double productPrice,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("totalPrice") double totalPrice,
            @JsonProperty("productId") Long productId,
            @JsonProperty("userId") String userId) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.productId = productId;
        this.userId = userId;
    }

    // Getter와 Setter
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
