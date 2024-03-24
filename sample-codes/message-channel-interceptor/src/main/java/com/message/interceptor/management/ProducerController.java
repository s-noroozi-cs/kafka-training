package com.message.interceptor.management;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {
  @Autowired private StreamBridge streamBridge;

  @PostMapping("/messages/uuid")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void sendUuidMessage() {
    CloudStreamUtil.sendMessage(
        streamBridge, "messageProducer-out-0", UUID.randomUUID().toString());
  }
}
