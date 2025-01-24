package com.example.demo.repository;

import com.example.demo.model.Cart;
import com.example.demo.model.User;
import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // 특정 사용자의 모든 장바구니 항목 조회
    List<Cart> findByUser(User user);

    // 특정 사용자의 특정 상품에 대한 장바구니 항목 조회
    Optional<Cart> findByUserAndProduct(User user, Product product);
    
    // 특정 사용자의 signupId로 장바구니 항목 조회
    List<Cart> findByUser_SignupId(String signupId);
    Optional<Cart> findByUserSignupIdAndProductId(String signupId, Long productId);


}
