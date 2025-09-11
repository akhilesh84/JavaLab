package com.demo.utils;

import java.time.Duration;

public class ExponentialBackoffRetryPolicy implements RetryPolicy {
    @Override
    public <TInput> Duration getDelay(TInput tInput, Exception exception, int retryAttempt) {
        return null;
    }
}
