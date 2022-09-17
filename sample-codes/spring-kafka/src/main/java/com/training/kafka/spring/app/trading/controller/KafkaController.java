package com.training.kafka.spring.app.trading.controller;

import com.training.kafka.spring.app.trading.config.MyListenableFutureCallback;
import com.training.kafka.spring.app.trading.model.Action;
import com.training.kafka.spring.app.trading.model.ConsumerActionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/controller")
public class KafkaController {

    private Logger logger = LoggerFactory.getLogger(KafkaController.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @PostMapping("/messages/{topic}")
    public ResponseEntity sendSingleMessage(@PathVariable("topic") String topic, @RequestBody String message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.addCallback(MyListenableFutureCallback.INSTANCE);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/consumers/{id}")
    public ResponseEntity manageConsumer(@PathVariable("id") String id,
                                         @RequestBody ConsumerActionRequest actionRequest) {

        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(id);

        if (listenerContainer == null)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("consumer id: " + id + " is not valid");

        if (!actionRequest.hasValidAction())
            return ResponseEntity.badRequest().body("Action request is not valid.");


        if (Action.Start.equals(actionRequest.getAction())) {
            listenerContainer.start();
        } else {
            listenerContainer.stop(() -> {
                logger.info("Consumer with id: " + id + " stopped successfully.");
            });
        }

        return ResponseEntity.ok().build();
    }
}
