package com.training.kafka.spring.app.trading.listener;

import com.training.kafka.spring.app.trading.config.ShareContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SingleMessageListener implements ConsumerSeekAware {
    private Logger logger = LoggerFactory.getLogger(SingleMessageListener.class);
    @Value("${kafka.consumer.id:singleConsumer}")
    private String consumerId;


    @KafkaListener(id = "${kafka.consumer.id:singleConsumer}"
            , autoStartup = "false"
            ,concurrency = "1"
            , topics = "${kafka.consumer.topic:test}")
    public void listen(Message<String> message) {
        logger.info("receive message: " + message.toString());
    }


    @Override
    public void registerSeekCallback(ConsumerSeekCallback callback) {
        ShareContainer.consumerCallBackStore.put(consumerId, callback);
    }
}
