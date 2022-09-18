package com.training.kafka.spring.app.trading.service;

import com.training.kafka.spring.app.trading.controller.KafkaController;
import com.training.kafka.spring.app.trading.model.Action;
import com.training.kafka.spring.app.trading.model.ConsumerActionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerService {
    private Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);

    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    public KafkaListenerService(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    private void startConsumer(MessageListenerContainer listenerContainer){
        listenerContainer.start();
    }

    private void stopConsumer(MessageListenerContainer listenerContainer){
        listenerContainer.stop(() -> {
            logger.info("Consumer: " + listenerContainer.getListenerId() + " stopped successfully.");
        });
    }

    public void manageConsumer(String id, Action action){
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(id);

        switch (action){

            case Stop:
                stopConsumer(listenerContainer);
                break;

            case Start:
                startConsumer(listenerContainer);
                break;
        }
    }

    public boolean isValidMessageListenerContainer(String id){
        return kafkaListenerEndpointRegistry.getListenerContainer(id) != null;
    }


}
