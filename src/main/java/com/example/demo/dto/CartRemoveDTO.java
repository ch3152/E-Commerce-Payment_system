package com.example.demo.dto;

public class CartRemoveDTO {
    private String userId;
    private Long productId;


    public CartRemoveDTO(String userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

  
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
}
