package com.example.demo.controller;

import com.example.demo.service.CartCleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart1")
public class CartDeleteController {

    private final CartCleanupService cartCleanupService; // CartCleanupService 주입

    @Autowired
    public CartDeleteController(CartCleanupService cartCleanupService) {
        this.cartCleanupService = cartCleanupService; // CartCleanupService 초기화
    }

    // 결제 후 장바구니에서 모든 상품 삭제
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteCart(@RequestBody Map<String, String> request) {
        String signupId = request.get("signupId"); // signupId를 클라이언트에서 받음
        if (signupId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Signup ID is missing"));
        }

        try {
            // signupId를 이용해 장바구니 항목 삭제
            String result = cartCleanupService.removeCartItemsAfterPayment(signupId); // CartCleanupService 호출
            return ResponseEntity.ok(Map.of("success", true, "message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
