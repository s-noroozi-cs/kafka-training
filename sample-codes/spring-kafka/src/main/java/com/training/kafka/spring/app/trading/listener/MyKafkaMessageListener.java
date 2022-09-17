package com.training.kafka.spring.app.trading.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MyKafkaMessageListener {

    private Logger logger = LoggerFactory.getLogger(MyKafkaMessageListener.class);

    @KafkaListener(id = "${kafka.consumer.id:myKafkaMsgConsumer}"
            , autoStartup = "false"
            , topics = "${kafka.consumer.topic:test}")
    public void listen(Message<String> message) {
        logger.info("receive message: " + message.toString());
    }

}
