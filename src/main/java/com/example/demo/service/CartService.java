package com.example.demo.service;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository, RedisService redisService, RabbitTemplate rabbitTemplate) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.rabbitTemplate = rabbitTemplate;
    }

    // 장바구니에 상품 추가
    public String addToCart(String signupId, Long productId, int quantity) {
        User user = findUserBySignupId(signupId);
        Product product = findProductById(productId);

        Optional<Cart> existingCart = cartRepository.findByUserAndProduct(user, product);

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
        } else {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(quantity);
        }

        // Redis에 장바구니 정보 저장
        saveCartToRedis(signupId);

        // RabbitMQ로 카트 변경사항 전송
        sendCartUpdateMessageToQueue(signupId);

        return "상품이 장바구니에 추가되었습니다.";
    }

    // 장바구니 항목 삭제
    public String removeCartItem(String signupId, Long productId) {
        User user = findUserBySignupId(signupId);
        Product product = findProductById(productId);

        // Redis에서 장바구니 항목 삭제
        redisService.removeCartItemFromRedis(signupId, productId);

        // DB에서 장바구니 항목 삭제
        cartRepository.findByUserAndProduct(user, product)
            .ifPresent(cartRepository::delete);

        // RabbitMQ로 카트 변경사항 전송
        sendCartUpdateMessageToQueue(signupId);

        return "장바구니에서 상품이 삭제되었습니다.";
    }

    // 카트 수량 업데이트
    public String updateCartItemQuantity(String signupId, Long productId, int quantity) {
        User user = findUserBySignupId(signupId);
        Product product = findProductById(productId);

        // Redis에서 장바구니 정보 업데이트
        redisService.updateCartItemQuantityInRedis(signupId, productId, quantity);

        // RabbitMQ로 카트 변경사항 전송
        sendCartUpdateMessageToQueue(signupId);

        return "수량이 업데이트되었습니다.";
    }

    // 장바구니 항목 조회 (userId로)
    public List<CartItemDTO> getCartItems(String userId) {
        // Redis에서 먼저 조회
        List<CartItemDTO> cartItems = redisService.getCartItemsFromRedis(userId);

        // Redis에 데이터가 없으면 DB에서 조회
        if (cartItems == null || cartItems.isEmpty()) {
            cartItems = cartRepository.findByUser(findUserBySignupId(userId))
                    .stream()
                    .map(cart -> new CartItemDTO(
                            cart.getProduct().getName(),
                            cart.getProduct().getPrice(),
                            cart.getQuantity(),
                            cart.getProduct().getPrice() * cart.getQuantity(),
                            cart.getProduct().getId(),
                            userId))
                    .collect(Collectors.toList());

            // 조회한 데이터를 Redis에 저장
            redisService.saveCartItemsToRedis(userId, cartItems);
        }

        return cartItems;
    }

    // 상품 찾기
    private Product findProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    // 사용자 찾기
    private User findUserBySignupId(String signupId) {
        return userRepository.findBySignupId(signupId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    // Redis에 장바구니 정보 저장
    private void saveCartToRedis(String signupId) {
        List<CartItemDTO> cartItems = cartRepository.findByUser(findUserBySignupId(signupId))
                .stream()
                .map(cart -> new CartItemDTO(
                        cart.getProduct().getName(),
                        cart.getProduct().getPrice(),
                        cart.getQuantity(),
                        cart.getProduct().getPrice() * cart.getQuantity(),
                        cart.getProduct().getId(),
                        signupId))
                .collect(Collectors.toList());

        redisService.saveCartItemsToRedis(signupId, cartItems);
    }

    // RabbitMQ로 카트 업데이트 메시지 전송
    private void sendCartUpdateMessageToQueue(String signupId) {
        String message = "Cart updated for user: " + signupId;
        // 중복 메시지를 방지하는 처리 (예: 메시지 고유 ID 생성)
        String messageId = generateMessageId(signupId);
        if (!isMessageAlreadyInQueue(messageId)) {
            rabbitTemplate.convertAndSend("cartExchange", "cart.update", message);
            logMessageSent(messageId);
        }
    }

    // 고유 메시지 ID 생성
    private String generateMessageId(String signupId) {
        return "cart-" + signupId + "-" + System.currentTimeMillis();
    }

    // 이미 큐에 메시지가 있는지 확인
    private boolean isMessageAlreadyInQueue(String messageId) {
        // 메시지 큐에서 중복 메시지가 있는지 확인하는 로직을 추가합니다.
        return false; // 실제로 큐와 연동하여 중복 체크 로직을 구현해야 합니다.
    }

    // 메시지 전송 로그
    private void logMessageSent(String messageId) {
        // 로그로 메시지 전송 여부를 기록합니다.
        System.out.println("Message sent with ID: " + messageId);
    }
}
