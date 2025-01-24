package com.example.demo.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.time.LocalDateTime;

@RedisHash(value = "web_payment_info", timeToLive = 600)
public class WebPaymentInfoRedisDto {

    @Id
    private Long id;
    private Long userId;
    private int totalPrice;
    private int totalQuantity;
    private String customerName;
    private String customerPhone;
    private String paymentStatus;
    private LocalDateTime createdAt;

    public WebPaymentInfoRedisDto(Long id, Long userId, int totalPrice, int totalQuantity, 
                                   String customerName, String customerPhone, String paymentStatus, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    // Getter와 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
