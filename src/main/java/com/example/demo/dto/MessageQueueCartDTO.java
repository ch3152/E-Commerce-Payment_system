package com.example.demo.dto;

public class MessageQueueCartDTO {
    private String userId;        // 사용자 ID
    private Long productId;       // 상품 ID
    private int quantity;         // 변경된 수량
    private String actionType;    // 작업 유형 (ADD, UPDATE, DELETE)

    // 기본 생성자
    public MessageQueueCartDTO() {}

    // 모든 필드를 포함한 생성자
    public MessageQueueCartDTO(String userId, Long productId, int quantity, String actionType) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.actionType = actionType;
    }

    // Getter 및 Setter
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "MessageQueueCartDTO{" +
                "userId='" + userId + '\'' +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", actionType='" + actionType + '\'' +
                '}';
    }
}
