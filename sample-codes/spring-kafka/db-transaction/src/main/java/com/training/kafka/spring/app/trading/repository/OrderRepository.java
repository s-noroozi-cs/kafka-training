package com.training.kafka.spring.app.trading.repository;

import com.training.kafka.spring.app.trading.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order,Long> {
}
