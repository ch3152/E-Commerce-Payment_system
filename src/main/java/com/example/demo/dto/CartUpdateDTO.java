
package com.example.demo.dto;

public class CartUpdateDTO {
    private String userId;
    private Long productId;
    private int change;

    // Constructor
    public CartUpdateDTO(String userId, Long productId, int change) {
        this.userId = userId;
        this.productId = productId;
        this.change = change;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }
}
