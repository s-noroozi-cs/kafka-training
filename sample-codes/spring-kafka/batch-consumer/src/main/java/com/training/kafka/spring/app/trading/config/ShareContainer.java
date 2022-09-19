package com.training.kafka.spring.app.trading.config;

import org.springframework.kafka.listener.ConsumerSeekAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShareContainer {
    public static final Map<String, ConsumerSeekAware.ConsumerSeekCallback> consumerCallBackStore =
            new ConcurrentHashMap<>();
}
