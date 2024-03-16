package com.message.interceptor.management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@GlobalChannelInterceptor
@Slf4j
@Component
public class MyChannelInterceptor implements ChannelInterceptor {
  private void log(String eventName) {
    log.info("""
\n---------------------- %s ----------------------
""".formatted(eventName));
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    log("preSend");
    return ChannelInterceptor.super.preSend(message, channel);
  }

  @Override
  public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    log("postSend");
    ChannelInterceptor.super.postSend(message, channel, sent);
  }

  @Override
  public void afterSendCompletion(
      Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
    log("afterSendCompletion");
    ChannelInterceptor.super.afterSendCompletion(message, channel, sent, ex);
  }

  @Override
  public boolean preReceive(MessageChannel channel) {
    log("preReceive");
    return ChannelInterceptor.super.preReceive(channel);
  }

  @Override
  public Message<?> postReceive(Message<?> message, MessageChannel channel) {
    log("postReceive");
    return ChannelInterceptor.super.postReceive(message, channel);
  }

  @Override
  public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
    log("afterReceiveCompletion");
    ChannelInterceptor.super.afterReceiveCompletion(message, channel, ex);
  }
}
