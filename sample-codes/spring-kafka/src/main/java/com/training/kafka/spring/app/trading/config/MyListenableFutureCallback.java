package com.training.kafka.spring.app.trading.config;

import org.springframework.util.concurrent.ListenableFutureCallback;

public class MyListenableFutureCallback implements ListenableFutureCallback {
    @Override
    public void onFailure(Throwable ex) {
        
    }

    @Override
    public void onSuccess(Object result) {

    }
}
