package com.chatapp.messageservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "chat-exchange";
    public static final String QUEUE_NAME = "message-published-queue";

    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue messageQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue messageQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(messageQueue).to(chatExchange).with("message.published");
    }
}