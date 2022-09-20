package com.training.kafka.spring.app.trading.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class MyTopicConfig {

    private static final String ORDERS = "orders";

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(ORDERS).partitions(1).replicas(1).build();
    }
}
