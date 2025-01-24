package com.example.demo.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.time.LocalDateTime;

@RedisHash(value = "toss_payment_info", timeToLive = 600)
public class TossPaymentInfoRedisDto {

    @Id
    private Long id;
    private String paymentMethod;
    private String currency;
    private String country;
    private int totalAmount;
    private String customerName;
    private String customerPhone;
    private LocalDateTime paymentTime;
    private String discount;

    public TossPaymentInfoRedisDto(Long id, String paymentMethod, String currency, String country, int totalAmount, 
                                    String customerName, String customerPhone, LocalDateTime paymentTime, String discount) {
        this.id = id;
        this.paymentMethod = paymentMethod;
        this.currency = currency;
        this.country = country;
        this.totalAmount = totalAmount;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.paymentTime = paymentTime;
        this.discount = discount;
    }

    // Getter와 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }
    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }
}
