/* 
package com.example.demo.dto;

public class CartSummaryDTO {
    private int totalPrice;  // 결제 총 가격 (정수형)
    private long totalQuantity; // 결제 총 수량

    // 기본 생성자
    public CartSummaryDTO() {
    }

    // 매개변수가 있는 생성자
    public CartSummaryDTO(int totalPrice, long totalQuantity) {
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
    }

    // Getter와 Setter
    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    // 결제 정보를 서버로 보내기 위한 메서드
    public static CartSummaryDTO fromCheckoutRequest(int totalPrice, long totalQuantity) {
        return new CartSummaryDTO(totalPrice, totalQuantity);
    }
}
*/