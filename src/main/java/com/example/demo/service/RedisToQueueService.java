package com.example.demo.service;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.config.RabbitMQConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class RedisToQueueService {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RedisToQueueService.class);
    private final ObjectMapper objectMapper;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisToQueueService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper,
                               CartRepository cartRepository, UserRepository userRepository,
                               ProductRepository productRepository, RedisTemplate<String, String> redisTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void processCartItem(CartItemDTO cartItemDTO) {
        logger.info("사용자 ID: {} 및 상품 ID: {}에 대해 장바구니 항목 처리 중", cartItemDTO.getUserId(), cartItemDTO.getProductId());

        try {
            validateRedisData(cartItemDTO);

            // 유저 조회
            User user = userRepository.findBySignupId(cartItemDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. signupId: " + cartItemDTO.getUserId()));

            // 상품 조회
            Product product = productRepository.findById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + cartItemDTO.getProductId()));

            Cart cart = cartRepository.findByUserSignupIdAndProductId(cartItemDTO.getUserId(), cartItemDTO.getProductId())
                .orElse(null);

            logger.info("사용자 ID: {}, 상품 ID: {}에 대한 장바구니 데이터 -> {}", 
                cartItemDTO.getUserId(), cartItemDTO.getProductId(), cart != null ? cart : "장바구니 항목 없음");

            if (cart != null) {
                // 기존 항목 업데이트
                cart.setQuantity(cartItemDTO.getQuantity());
                cartRepository.save(cart);
                logger.info("장바구니 항목 업데이트: {}", cart);
            } else {
                // 새로운 항목 추가
                Cart newCart = new Cart(user, product, cartItemDTO.getQuantity());
                cartRepository.save(newCart);
                logger.info("새로운 장바구니 항목 저장: {}", newCart);
            }

            // 큐로 데이터 전송
            sendCartDataToQueue(cartItemDTO);

        } catch (IllegalArgumentException e) {
            logger.error("검증 실패: {}", e.getMessage());
            throw e;  // 트랜잭션 롤백을 위해 예외를 던집니다.
        } catch (Exception e) {
            logger.error("장바구니 항목 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("장바구니 항목 처리 중 오류 발생", e);  // 트랜잭션 롤백을 위해 예외를 던집니다.
        }

        logger.info("사용자 ID: {} 및 상품 ID: {}에 대해 장바구니 항목 처리 완료", cartItemDTO.getUserId(), cartItemDTO.getProductId());
    }

    @Async
    public void sendCartDataToQueue(CartItemDTO cartItemDTO) {
        logger.info("장바구니 항목 큐로 전송: {}", cartItemDTO);

        try {
            if (cartItemDTO == null) {
                logger.warn("null 장바구니 항목을 큐로 전송할 수 없습니다.");
                return;
            }
            String cartItemJson = objectMapper.writeValueAsString(cartItemDTO);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, cartItemJson);
            logger.info("장바구니 항목 큐로 전송 성공: {}", cartItemJson);
        } catch (JsonProcessingException e) {
            logger.error("메세지큐: 장바구니 항목 직렬화 실패: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("메세지큐: 장바구니 항목 큐로 전송 실패: {}", e.getMessage(), e);
        }
    }

    private void validateRedisData(CartItemDTO cartItemDTO) {
        logger.info("데이터 검증 중: 사용자 ID={}, 상품 ID={}, 수량={}",
                cartItemDTO.getUserId(), cartItemDTO.getProductId(), cartItemDTO.getQuantity());

        if (cartItemDTO.getUserId() == null || cartItemDTO.getProductId() == null || cartItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Redis에서 받은 잘못된 장바구니 데이터.");
        }

        logger.info("데이터 검증 성공: {}", cartItemDTO);
    }

    public void processCartDataFromRedis(String userId) {
        String redisKey = "cart:" + userId;
        String redisData = redisTemplate.opsForValue().get(redisKey);

        if (redisData == null || redisData.isEmpty()) {
            logger.warn("메세지큐: Redis에서 키: {}에 대한 데이터 없음", redisKey);
            return;
        }

        try {
            CartItemDTO cartItemDTO = objectMapper.readValue(redisData, CartItemDTO.class);
            logger.info("메세지큐: Redis에서 받은 DTO: {}", cartItemDTO);

            processCartItem(cartItemDTO);

        } catch (JsonProcessingException e) {
            logger.error("메세지큐: 사용자 ID: {}의 Redis 데이터 파싱 오류: {}", userId, e.getMessage(), e);
        }
    }
}
