package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.TossPaymentInfo;
import com.example.demo.repository.TossPaymentInfoRepository;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class TossService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TossPaymentInfoRepository paymentRepository;
    private final UserService userService;  // UserService 주입

    @Autowired
    public TossService(TossPaymentInfoRepository paymentRepository, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
    }

    @Async
    public void savePaymentInfo(String paymentMethod, String currency, String country, int totalAmount, String discount, Timestamp paymentTime, String signupId) {
        try {
            logger.info("Saving payment info to DB: Method={}, Currency={}, Country={}, Amount={}, Discount={}, Time={}, SignupId={}",
                    paymentMethod, currency, country, totalAmount, discount, paymentTime, signupId);

            // 사용자 정보를 가져옵니다.
            User user = userService.getUserBySignupId(signupId);
            if (user == null) {
                logger.error("User with signupId {} not found", signupId);
                return;
            }

            // TossPaymentInfo 객체에 결제 정보 설정
            TossPaymentInfo paymentInfo = new TossPaymentInfo();
            paymentInfo.setPaymentMethod(paymentMethod);
            paymentInfo.setCurrency(currency);
            paymentInfo.setCountry(country);
            paymentInfo.setTotalAmount(totalAmount);
            
            // 할인 정보는 null 체크 후 처리
            paymentInfo.setDiscount(discount != null ? discount : "{}"); // JSON 형식으로 기본값 설정
            paymentInfo.setPaymentTime(paymentTime);
            paymentInfo.setCustomerName(user.getName());  // 사용자 이름 설정
            paymentInfo.setCustomerPhone(user.getPhone());  // 사용자 전화번호 설정

            // DB에 결제 정보 저장
            paymentRepository.save(paymentInfo);
            logger.info("Payment info saved successfully.");
        } catch (Exception e) {
            logger.error("Error while saving payment info: {}", e.getMessage(), e);
        }
    }
}
