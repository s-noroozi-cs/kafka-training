package com.message.interceptor.management;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;

public class CloudStreamUtil {
  public static boolean sendMessage(StreamBridge streamBridge, String destination, String message) {
    return streamBridge.send(
        destination,
        MessageBuilder.withPayload(message).setHeader("x-topic-name", destination).build());
  }
}
