package com.message.interceptor.management;

import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
public class MessageConsumerListener {
  @Bean
  public Consumer<Message> messageConsumer() {
    return message -> {};
  }
}
