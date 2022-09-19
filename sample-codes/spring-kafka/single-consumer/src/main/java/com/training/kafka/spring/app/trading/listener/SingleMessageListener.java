package com.training.kafka.spring.app.trading.listener;

import com.training.kafka.spring.app.trading.config.ShareContainer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SingleMessageListener implements ConsumerSeekAware {
    private Logger logger = LoggerFactory.getLogger(SingleMessageListener.class);
    @Value("${kafka.batch.consumer.id:singleConsumer}")
    private String consumerId;


    @KafkaListener(id = "${kafka.batch.consumer.id:singleConsumer}"
            , autoStartup = "false"
            , topics = "${kafka.consumer.topic:test}")
    public void listen(Message<String> message) {
        logger.info("receive message: " + message);
    }

    @Override
    public void registerSeekCallback(ConsumerSeekCallback callback) {
        ShareContainer.consumerCallBackStore.put(consumerId, callback);
    }
}
