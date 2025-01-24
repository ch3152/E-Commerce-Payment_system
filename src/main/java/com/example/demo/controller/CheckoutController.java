package com.example.demo.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/sss")
public class CheckoutController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 결제 페이지로 이동
    @GetMapping("/checkout")
    public String checkoutPage() {
        return "checkout";  
    }

    // 클라이언트에서 결제 정보 받기 (totalPrice, totalQuantity)
    @PostMapping("/submit")
    public ResponseEntity<String> checkout(@RequestBody String jsonBody, HttpServletRequest request) {
        // 클라이언트에서 전달한 JSON 데이터를 파싱
        JSONObject requestData = new JSONObject(jsonBody);

        // totalPrice와 totalQuantity를 JSON에서 추출
        int totalPrice = requestData.getInt("totalPrice");
        long totalQuantity = requestData.getLong("totalQuantity");

        // 받은 데이터를 세션에 저장
        request.getSession().setAttribute("totalPrice", totalPrice);
        request.getSession().setAttribute("totalQuantity", totalQuantity);

        // 로그에 출력
        logger.info("총 가격: {}, 총 수량: {}", totalPrice, totalQuantity);

        // 결제 준비 완료 응답
        return ResponseEntity.ok("결제 준비가 완료되었습니다.");
    }
}
