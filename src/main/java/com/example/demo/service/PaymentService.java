package com.example.demo.service;

import com.example.demo.model.WebPaymentInfo;
import com.example.demo.repository.WebPaymentInfoRepository;
import com.example.demo.model.User;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final WebPaymentInfoRepository paymentRepository;
    private final UserService userService;

    public PaymentService(WebPaymentInfoRepository paymentRepository, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
    }

    // 결제 정보 저장 메서드
    public void webPaymentInfo(String signupId, int totalPrice, long totalQuantity, String paymentStatus) {
        // 사용자 정보 가져오기
        User user = userService.getUserBySignupId(signupId);
        
        // WebPaymentInfo 객체 생성
        WebPaymentInfo paymentInfo = new WebPaymentInfo(
            user,               // user
            totalPrice,         // totalPrice
            totalQuantity,      // totalQuantity
            user.getName(),     // customerName (사용자 이름)
            user.getPhone(),    // customerPhone (사용자 전화번호)
            paymentStatus       // paymentStatus
        );             
        // DB에 결제 정보 저장
        paymentRepository.save(paymentInfo);
    }
}
