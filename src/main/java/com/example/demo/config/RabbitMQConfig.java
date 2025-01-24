package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.example.demo.service.CartMessageListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "cart-queue";  
    public static final String EXCHANGE_NAME = "cartExchange";
    public static final String ROUTING_KEY = "cart.#";

    // Queue 선언
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); 
    }

    // Exchange 설정
    @Bean
    public Exchange cartExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    // Queue와 Exchange 바인딩
    @Bean
    public Binding binding(Queue queue, Exchange cartExchange) {
        return BindingBuilder.bind(queue)
                .to((TopicExchange) cartExchange)
                .with(ROUTING_KEY);
    }

    // Jackson 메시지 변환기 설정
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // RabbitTemplate 설정
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    // 메시지 리스너 설정
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME); 
        container.setMessageListener(listenerAdapter);
        container.setConcurrentConsumers(1);  
        container.setMaxConcurrentConsumers(5); 
        return container;
    }

    // 메시지 리스너 어댑터 설정
    @Bean
    public MessageListenerAdapter listenerAdapter(CartMessageListener listener, Jackson2JsonMessageConverter converter) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(listener, "onMessage");
        adapter.setMessageConverter(converter);
        return adapter;
    }

    // ObjectMapper 설정 (날짜 포맷 및 파라미터 이름 모듈 추가)
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); 
        objectMapper.registerModule(new ParameterNamesModule());  
        return objectMapper;
    }
}