package com.message.interceptor.management;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
@Slf4j
public class MessageConsumerListener {
  @Bean
  public Consumer<Message<String>> messageConsumer() {
    return message -> {
      StringBuilder headers = new StringBuilder();
      message.getHeaders().forEach((k, v) -> headers.append("%s: %s".formatted(k, v)));
      log.info(
          """

================ receive new kafka message ================
 message payload: %s
 message headers: %s
"""
              .formatted(message.getPayload(), headers));
    };
  }
}
