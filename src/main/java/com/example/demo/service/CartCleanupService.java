package com.example.demo.service;

import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartCleanupService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository; // ProductRepository 주입

    @Autowired
    public CartCleanupService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository; // 초기화
    }

    // 장바구니에서 특정 사용자 모든 상품 삭제 및 상품 수량 업데이트
    public String removeCartItemsAfterPayment(String signupId) {
        // 사용자가 존재하는지 확인
        User user = userRepository.findBySignupId(signupId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. signupId: " + signupId));

        List<Cart> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            return "장바구니가 비어 있습니다.";
        }

        // 각 장바구니 항목에 대해 상품 수량 차감
        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantityToReduce = cartItem.getQuantity();
            product.setQuantity(product.getQuantity() - quantityToReduce);  // 상품 수량 차감

            // 상품 수량 업데이트
            productRepository.save(product);
        }

        // 장바구니 항목 삭제
        cartRepository.deleteAll(cartItems);

        return "결제 후 장바구니에서 모든 상품이 삭제되고, 상품 수량이 차감되었습니다.";
    }
}
