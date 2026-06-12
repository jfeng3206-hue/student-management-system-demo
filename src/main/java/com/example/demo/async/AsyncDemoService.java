package com.example.demo.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncDemoService {

    private static final Logger log = LoggerFactory.getLogger(AsyncDemoService.class);

    @Async
    public void simulateTask(String operation) {
        log.info("[ASYNC] Starting background task for operation: {}", operation);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("[ASYNC] Completed background task for operation: {}", operation);
    }
}
