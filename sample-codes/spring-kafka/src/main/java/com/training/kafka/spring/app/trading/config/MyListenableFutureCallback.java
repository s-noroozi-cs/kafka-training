package com.training.kafka.spring.app.trading.config;

import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class MyListenableFutureCallback implements ListenableFutureCallback<SendResult<String, String>> {
    public static final MyListenableFutureCallback INSTANCE = new MyListenableFutureCallback();
    @Override
    public void onFailure(Throwable ex) {
        
    }

    @Override
    public void onSuccess(SendResult<String, String> result) {

    }
}
