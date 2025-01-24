package com.example.demo.service;

import com.example.demo.dto.CartItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CartMessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(CartMessageListener.class);

    @RabbitListener(queues = "cart-queue") // 큐 이름을 cart-queue로 수신
    public void onMessage(String message) {
        logger.info("[onMessage] RabbitMQ에서 메시지 수신: {}", message);

        try {
            if (message.trim().startsWith("{")) {
                logger.info("[onMessage] JSON 형식 메시지 감지.");

                // JSON 메시지 변환
                CartItemDTO[] cartItems = objectMapper.readValue(message, CartItemDTO[].class);
                logger.info("[onMessage] 메시지에서 추출한 장바구니 항목: {}", Arrays.toString(cartItems));

                // 메시지 처리 로직 추가 가능 (예: DB 업데이트)
            } else {
                logger.warn("[onMessage] 비 JSON 메시지 수신: {}", message);
            }
        } catch (Exception e) {
            logger.error("[onMessage] 메시지를 처리하는 중 오류 발생", e);
        }
    }
}
