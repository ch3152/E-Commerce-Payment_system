package com.example.demo.service;

import com.example.demo.dto.CartItemDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisService {

    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate, 
                        ObjectMapper objectMapper,
                        RabbitTemplate rabbitTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void updateCartItemQuantityInRedis(String signupId, Long productId, int quantity) {
        String key = "cart:" + signupId;
        List<CartItemDTO> cartItems = getCartItemsFromRedis(signupId);

        if (cartItems != null) {
            boolean updated = false;
            for (CartItemDTO cartItem : cartItems) {
                if (cartItem.getProductId().equals(productId)) {
                    cartItem.setQuantity(quantity);
                    updated = true;
                    break;
                }
            }
            if (updated) {
                saveCartItemsToRedis(signupId, cartItems);
                sendCartDataToQueue(cartItems); 
            }
        }
    }

    public List<CartItemDTO> getCartItemsFromRedis(String signupId) {
        String key = "cart:" + signupId;
        String cartJson = redisTemplate.opsForValue().get(key);
        if (cartJson != null) {
            try {
                return objectMapper.readValue(cartJson, objectMapper.getTypeFactory().constructCollectionType(List.class, CartItemDTO.class));
            } catch (JsonProcessingException e) {
                logger.error("Error parsing cart JSON for signupId={}", signupId, e);
            }
        }
        return new ArrayList<>();
    }

    public void saveCartItemsToRedis(String signupId, List<CartItemDTO> cartItems) {
        String key = "cart:" + signupId;
        try {
            String cartJson = objectMapper.writeValueAsString(cartItems);
            redisTemplate.opsForValue().set(key, cartJson);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing cart items for signupId={}", signupId, e);
        }
    }

    public void removeCartItemFromRedis(String signupId, Long productId) {
        String key = "cart:" + signupId;
        List<CartItemDTO> cartItems = getCartItemsFromRedis(signupId);

        if (cartItems != null) {
            cartItems.removeIf(cartItem -> cartItem.getProductId().equals(productId));
            saveCartItemsToRedis(signupId, cartItems);
        }
    }

    // 큐로 카트 데이터를 전송하는 메서드
    private void sendCartDataToQueue(List<CartItemDTO> cartItems) {
        try {
            String cartItemsJson = objectMapper.writeValueAsString(cartItems);
            rabbitTemplate.convertAndSend("exchangeName", "routingKey", cartItemsJson);
            logger.info("Sent cart items to queue: {}", cartItemsJson);
        } catch (JsonProcessingException e) {
            logger.error("Error sending cart items to queue", e);
        }
    }
}
