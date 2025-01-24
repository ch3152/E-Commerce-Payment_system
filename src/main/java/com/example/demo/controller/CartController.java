package com.example.demo.controller;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 장바구니에 상품 추가
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartRequest request) {
        try {
            String message = cartService.addToCart(request.getSignupId(), request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"알 수 없는 오류가 발생했습니다.\"}");
        }
    }

    // 장바구니 수량 업데이트
    @PostMapping("/update")
    public ResponseEntity<String> updateCart(@RequestBody CartUpdateRequest request) {
        try {
            String message = cartService.updateCartItemQuantity(request.getSignupId(), request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"알 수 없는 오류가 발생했습니다.\"}");
        }
    }

    // 장바구니에서 상품 삭제
    @PostMapping("/remove")
    public ResponseEntity<String> removeFromCart(@RequestBody CartUpdateRequest request) {
        try {
            String message = cartService.removeCartItem(request.getSignupId(), request.getProductId());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"알 수 없는 오류가 발생했습니다.\"}");
        }
    }

    // 장바구니 조회
    @GetMapping("/items")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@RequestParam("userId") String userId) {
        try {
            List<CartItemDTO> items = cartService.getCartItems(userId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 결제 요청을 처리하기 위한 클래스
    public static class PaymentRequest {
        private int totalPrice;
        private int totalQuantity;

        public int getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(int totalPrice) {
            this.totalPrice = totalPrice;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(int totalQuantity) {
            this.totalQuantity = totalQuantity;
        }
    }

    // 장바구니 추가 요청을 처리하기 위한 클래스
    public static class CartRequest {
        private String signupId;
        private Long productId;
        private int quantity;

        public String getSignupId() {
            return signupId;
        }

        public void setSignupId(String signupId) {
            this.signupId = signupId;
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
    }

    // 장바구니 업데이트 요청을 처리하기 위한 클래스
    public static class CartUpdateRequest {
        private String signupId;
        private Long productId;
        private int quantity;

        public String getSignupId() {
            return signupId;
        }

        public void setSignupId(String signupId) {
            this.signupId = signupId;
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
    }
}
