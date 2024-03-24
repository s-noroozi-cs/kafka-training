package com.message.interceptor.management;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.cloud.stream.messaging.DirectWithAttributesChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptor;

@Slf4j
@AllArgsConstructor
public class MyChannelInterceptor implements ChannelInterceptor {

  private final BindingServiceProperties bindingServiceProperties;
  private final String channelType;

  enum EVENT {
    PRE_SEND,
    POST_SEND,
    AFTER_SEND_COMPLETION,
    PRE_RECEIVE,
    POST_RECEIVE,
    AFTER_RECEIVE_COMPLETION
  }

  private void log(EVENT event, MessageChannel channel) {
    log(event, null, channel, null, null);
  }

  private void log(EVENT event, Message<?> message, MessageChannel channel) {
    log(event, message, channel, null, null);
  }

  private void log(EVENT event, Message<?> message, MessageChannel channel, Boolean sent) {
    log(event, message, channel, sent, null);
  }

  private void log(EVENT event, Message<?> message, MessageChannel channel, Exception ex) {
    log(event, message, channel, null, ex);
  }

  private void log(
      EVENT event, Message<?> message, MessageChannel channel, Boolean sent, Exception ex) {

    String channelName =
        channel instanceof DirectWithAttributesChannel directWithAttributesChannel
            ? directWithAttributesChannel.getFullChannelName()
            : "";
    String topicName =
        "fetch binding from header and then transform to destination by binding properties";

    String headers =
        Optional.ofNullable(message)
            .map(Message::getHeaders)
            .map(MessageHeaders::entrySet)
            .map(Set::stream)
            .orElse(Stream.empty())
            .map(i -> String.format("%s: %s", i.getKey(), i.getValue()))
            .collect(Collectors.joining("\n", "[", "]"));

    log.info(
        """
\n---------------------- %s ----------------------
channel type: %s
message: %s
headers: %s
sent: %b
Exception: %s
"""
            .formatted(
                event.name(),
                channelType,
                Optional.ofNullable(message).map(Message::getPayload).orElse(null) + "",
                headers,
                Optional.ofNullable(sent).orElse(false),
                Optional.ofNullable(ex).map(Exception::getMessage).orElse("")));
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    log(EVENT.PRE_SEND, message, channel);
    return ChannelInterceptor.super.preSend(message, channel);
  }

  @Override
  public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    log(EVENT.POST_SEND, message, channel, sent);
    ChannelInterceptor.super.postSend(message, channel, sent);
  }

  @Override
  public void afterSendCompletion(
      Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
    log(EVENT.AFTER_SEND_COMPLETION, message, channel, sent, ex);
    ChannelInterceptor.super.afterSendCompletion(message, channel, sent, ex);
  }

  @Override
  public boolean preReceive(MessageChannel channel) {
    log(EVENT.PRE_RECEIVE, channel);
    return ChannelInterceptor.super.preReceive(channel);
  }

  @Override
  public Message<?> postReceive(Message<?> message, MessageChannel channel) {
    log(EVENT.POST_RECEIVE, message, channel);
    return ChannelInterceptor.super.postReceive(message, channel);
  }

  @Override
  public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
    log(EVENT.AFTER_RECEIVE_COMPLETION, message, channel, ex);
    ChannelInterceptor.super.afterReceiveCompletion(message, channel, ex);
  }
}
