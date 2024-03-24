package com.message.interceptor.management;

import static com.message.interceptor.management.HeaderNames.CLOUD_STREAM_BINDING_NAME;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;

public class CloudStreamUtil {
  public static boolean sendMessage(StreamBridge streamBridge, String bindingName, String message) {
    return streamBridge.send(
        bindingName,
        MessageBuilder.withPayload(message)
            .setHeader(CLOUD_STREAM_BINDING_NAME, bindingName)
            .build());
  }
}
