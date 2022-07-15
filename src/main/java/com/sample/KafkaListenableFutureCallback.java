package com.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
public class KafkaListenableFutureCallback implements ListenableFutureCallback<SendResult<String, String>> {
    @Override
    public void onFailure(Throwable throwable) {
        log.error("cannot send message", throwable);
    }

    @Override
    public void onSuccess(SendResult<String, String> stringSendResult) {
        log.info("message sent success!");
    }
}
