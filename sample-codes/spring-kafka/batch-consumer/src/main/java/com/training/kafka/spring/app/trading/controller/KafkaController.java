package com.training.kafka.spring.app.trading.controller;

import com.training.kafka.spring.app.trading.config.MyListenableFutureCallback;
import com.training.kafka.spring.app.trading.model.ConsumerActionRequest;
import com.training.kafka.spring.app.trading.service.KafkaListenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/controller")
public class KafkaController {

    private Logger logger = LoggerFactory.getLogger(KafkaController.class);

    private KafkaTemplate<String, String> kafkaTemplate;
    private KafkaListenerService service;

    @Autowired
    public KafkaController(KafkaTemplate<String, String> kafkaTemplate, KafkaListenerService service) {
        this.kafkaTemplate = kafkaTemplate;
        this.service = service;
    }

    @PostMapping("/messages/{topic}")
    public ResponseEntity sendSingleMessage(@PathVariable("topic") String topic, @RequestBody String message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.addCallback(MyListenableFutureCallback.INSTANCE);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/consumers/{id}")
    public ResponseEntity manageConsumer(@PathVariable("id") String id,
                                         @RequestBody ConsumerActionRequest actionRequest) {

        if (!actionRequest.hasValidAction())
            return ResponseEntity.badRequest().body("Action request is not valid.");

        if (!service.isValidMessageListenerContainer(id))
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("consumer: " + id + " is not valid");

        service.manageConsumer(id,actionRequest);

        return ResponseEntity.ok().build();
    }
}
