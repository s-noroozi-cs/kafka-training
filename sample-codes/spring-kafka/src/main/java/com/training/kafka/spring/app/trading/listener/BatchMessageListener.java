package com.training.kafka.spring.app.trading.listener;

import com.training.kafka.spring.app.trading.config.ShareContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.messaging.Message;

import java.util.List;

public class BatchMessageListener implements ConsumerSeekAware {

    private Logger logger = LoggerFactory.getLogger(SingleMessageListener.class);
    @Value("${kafka.consumer.id:batchConsumer}")
    private String consumerId;


    @KafkaListener(id = "${kafka.consumer.id:batchConsumer}"
            , autoStartup = "false"
            ,batch = "10"
            , topics = "${kafka.consumer.topic:test}")
    public void listen(List<Message<String>> messages) {
        logger.info("receive messages: " + messages.toString());
    }


    @Override
    public void registerSeekCallback(ConsumerSeekCallback callback) {
        ShareContainer.consumerCallBackStore.put(consumerId, callback);
    }

}
