package org.example;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "post_review_exchange";
    public static final String APPROVE_QUEUE = "approve_queue";
    public static final String REJECT_QUEUE = "reject_queue";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue approveQueue() {
        return new Queue(APPROVE_QUEUE);
    }

    @Bean
    public Queue rejectQueue() {
        return new Queue(REJECT_QUEUE);
    }

    @Bean
    public Queue dummyQueue() {
        return new Queue("dummy_queue");
    }

    @Bean
    public Binding approveBinding(Queue approveQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(approveQueue).to(topicExchange).with("post.approve");
    }

    @Bean
    public Binding rejectBinding(Queue rejectQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(rejectQueue).to(topicExchange).with("post.reject");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
