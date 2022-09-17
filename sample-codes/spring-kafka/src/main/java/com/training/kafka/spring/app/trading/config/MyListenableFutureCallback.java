package com.training.kafka.spring.app.trading.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class MyListenableFutureCallback implements ListenableFutureCallback<SendResult<String, String>> {
    private Logger logger = LoggerFactory.getLogger(MyListenableFutureCallback.class);
    public static final MyListenableFutureCallback INSTANCE = new MyListenableFutureCallback();

    @Override
    public void onFailure(Throwable ex) {
        logger.error("Failed to send message: " + ex.getMessage(),ex);
    }

    @Override
    public void onSuccess(SendResult<String, String> result) {
        logger.info("Successfully send message: " + result.toString());
    }
}
