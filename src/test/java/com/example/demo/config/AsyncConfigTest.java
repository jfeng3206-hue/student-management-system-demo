package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncConfigTest {

    @Test
    void nameAggregationExecutorUsesExpectedThreadPoolSettings() {
        AsyncConfig config = new AsyncConfig();

        Executor executor = config.nameAggregationExecutor();

        assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
        assertThat(taskExecutor.getCorePoolSize()).isEqualTo(2);
        assertThat(taskExecutor.getMaxPoolSize()).isEqualTo(4);
        assertThat(taskExecutor.getQueueCapacity()).isEqualTo(50);
        assertThat(taskExecutor.getThreadNamePrefix()).isEqualTo("name-aggregation-");
        taskExecutor.shutdown();
    }
}
