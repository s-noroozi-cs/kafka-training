package com.training.kafka.spring.app.trading.listener;

import com.training.kafka.spring.app.trading.entity.Order;
import com.training.kafka.spring.app.trading.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessor {
    Logger logger = LoggerFactory.getLogger(OrderProcessor.class);
    private OrderRepository repository;

    @Autowired
    public OrderProcessor(OrderRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "orders")
    public void process(String order) {
        logger.info("receive order: " + order);

        Order entity = repository.save(new Order(order));
        logger.info("persist order: " + entity);

        if("error".equals(order)) {
            logger.warn("manually handle specific scenario to rollback");
            throw new RuntimeException("rollback");
        }
    }

}
