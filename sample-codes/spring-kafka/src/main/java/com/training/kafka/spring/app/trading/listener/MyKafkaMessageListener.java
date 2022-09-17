package com.training.kafka.spring.app.trading.listener;

import org.apache.kafka.common.protocol.Message;
import org.springframework.kafka.annotation.KafkaListener;


public class MyKafkaMessageListener {


    @KafkaListener(id="${kafka.consumer.id:myKafkaMsgConsumer}"
            , autoStartup = "false"
            , topics = "${kafka.consumer.topic:test}")
    public void listen(Message message){
        System.out.println(message);
    }

}
