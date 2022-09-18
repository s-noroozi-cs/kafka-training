package com.training.kafka.spring.app.trading.service;

import com.training.kafka.spring.app.trading.config.ShareContainer;
import com.training.kafka.spring.app.trading.model.ConsumerActionRequest;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

@Service
public class KafkaListenerService {
    private Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);

    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    public KafkaListenerService(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    private void startConsumer(MessageListenerContainer listenerContainer) {
        listenerContainer.start();
    }

    private void stopConsumer(MessageListenerContainer listenerContainer) {
        listenerContainer.stop(() -> {
            logger.info("Consumer: " + listenerContainer.getListenerId() + " stopped successfully.");
        });
    }

    public void manageConsumer(String id, ConsumerActionRequest actionRequest) {
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(id);

        switch (actionRequest.getAction()) {

            case Stop:
                stopConsumer(listenerContainer);
                break;

            case Start:
                startConsumer(listenerContainer);
                break;

            case Seek:
                seekConsumer(id, actionRequest.getOffsetDateTimeAsLocalDateTime());
        }
    }

    public boolean isValidMessageListenerContainer(String id) {
        return kafkaListenerEndpointRegistry.getListenerContainer(id) != null;
    }

    private void seekConsumer(String id, LocalDateTime offsetDateTime) {
        Collection<TopicPartition> topicPartitions = kafkaListenerEndpointRegistry
                .getListenerContainer(id)
                .getAssignedPartitions();
        ZonedDateTime zdt = offsetDateTime.atZone(ZoneId.systemDefault());
        long milliSec = zdt.toInstant().toEpochMilli();

        ShareContainer.consumerCallBackStore
                .get(id)
                .seekToTimestamp(topicPartitions, milliSec);
    }


}
