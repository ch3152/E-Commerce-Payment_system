package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import com.example.demo.dto.CartItemDTO;

@Configuration
public class RedisConfig {

    // 기본적인 RedisTemplate 정의 (모든 객체에 대해 처리 가능)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Jackson을 이용하여 모든 객체를 JSON으로 직렬화
        RedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setDefaultSerializer(serializer);

        return template;
    }

    // CartItemDTO 타입에 대해 직렬화하는 RedisTemplate 정의
    @Bean
    public RedisTemplate<String, CartItemDTO> cartItemRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CartItemDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Jackson을 이용하여 CartItemDTO 객체를 JSON으로 직렬화
        RedisSerializer<CartItemDTO> serializer = new Jackson2JsonRedisSerializer<>(CartItemDTO.class);
        template.setDefaultSerializer(serializer);

        return template;
    }
}
