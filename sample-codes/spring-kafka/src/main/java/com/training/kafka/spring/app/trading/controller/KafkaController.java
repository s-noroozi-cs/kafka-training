package com.training.kafka.spring.app.trading.controller;

import com.training.kafka.spring.app.trading.config.MyListenableFutureCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/controller")
public class KafkaController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/messages/{topic}")
    public ResponseEntity sendSingleMessage(@PathVariable("topic") String topic, @RequestBody String message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.addCallback(new MyListenableFutureCallback());
        return ResponseEntity.ok().build();
    }
}
