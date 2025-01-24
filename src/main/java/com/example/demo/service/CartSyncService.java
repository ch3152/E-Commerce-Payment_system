package com.example.demo.service;

import com.example.demo.dto.CartItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartSyncService {

    private static final Logger logger = LoggerFactory.getLogger(CartSyncService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    // 1분마다 Redis에서 장바구니 데이터를 조회하여 메시지 큐에 보내는 작업
    @Scheduled(fixedRate = 60000)
    public void syncCartToQueue() {
        logger.info("[syncCartToQueue] 시작");

        try (Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match("cart:*").build())) {
            while (cursor.hasNext()) {
                String redisKey = cursor.next();
                logger.info("[syncCartToQueue] Redis 키 확인: {}", redisKey);

                List<CartItemDTO> cartItems = getCartItemsFromRedis(redisKey);
                if (!cartItems.isEmpty()) {
                    logger.info("[syncCartToQueue] Redis에서 가져온 장바구니 항목: {}", cartItems);
                    sendBatchToQueue(cartItems);
                } else {
                    logger.info("[syncCartToQueue] Redis에서 장바구니 항목을 찾을 수 없음: {}", redisKey);
                }
            }
        } catch (Exception e) {
            logger.error("[syncCartToQueue] Redis에서 장바구니를 동기화하는 중 오류 발생", e);
        }
    }

    // Redis에서 데이터를 가져와 CartItemDTO 리스트로 변환
    private List<CartItemDTO> getCartItemsFromRedis(String redisKey) {
        logger.info("[getCartItemsFromRedis] Redis 키로 데이터 조회: {}", redisKey);

        try {
            String cartJson = redisTemplate.opsForValue().get(redisKey);
            if (cartJson != null) {
                List<CartItemDTO> cartItems = objectMapper.readValue(cartJson, objectMapper.getTypeFactory().constructCollectionType(List.class, CartItemDTO.class));
                logger.info("[getCartItemsFromRedis] Redis에서 장바구니 항목 변환 성공: {}", cartItems);
                return cartItems;
            } else {
                logger.warn("[getCartItemsFromRedis] Redis에서 데이터를 찾을 수 없음: {}", redisKey);
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("[getCartItemsFromRedis] Redis 데이터를 파싱하는 중 오류 발생: {}", redisKey, e);
            return new ArrayList<>();
        }
    }

    // CartItemDTO 리스트를 JSON으로 변환하여 RabbitMQ에 전송
    private void sendBatchToQueue(List<CartItemDTO> cartItems) {
        try {
            String batchJson = objectMapper.writeValueAsString(cartItems);
            logger.info("[sendBatchToQueue] 전송할 메시지 생성: {}", batchJson);

            // 큐 이름을 "cart-queue"로 변경하여 메시지를 전송
            rabbitTemplate.convertAndSend("cartExchange", "cart.#", batchJson);
            logger.info("[sendBatchToQueue] 장바구니 항목을 큐로 전송 완료");
        } catch (Exception e) {
            logger.error("[sendBatchToQueue] RabbitMQ로 전송하는 중 오류 발생", e);
        }
    }
}