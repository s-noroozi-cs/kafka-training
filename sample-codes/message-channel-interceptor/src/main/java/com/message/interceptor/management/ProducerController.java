package com.message.interceptor.management;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {
  @Autowired private StreamBridge streamBridge;

  @Value("${spring.cloud.stream.bindings.messageProducer-out-0.destination}")
  private String messageDestination;

  @PostMapping("/messages/uuid")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void sendUuidMessage() {
    CloudStreamUtil.sendMessage(streamBridge, messageDestination, UUID.randomUUID().toString());
  }
}
