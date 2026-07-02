package com.example.demo.service.impl;

import com.example.demo.dto.NameAggregationRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NameAggregationServiceImplTest {

    @TempDir
    Path tempDir;

    @Test
    void forwardToNextUsesResilience4jAnnotations() throws NoSuchMethodException {
        Method method = NameAggregationServiceImpl.class.getMethod("forwardToNext", List.class);

        Retry retry = method.getAnnotation(Retry.class);
        CircuitBreaker circuitBreaker = method.getAnnotation(CircuitBreaker.class);

        assertThat(retry).isNotNull();
        assertThat(retry.name()).isEqualTo("nameAggregation");
        assertThat(circuitBreaker).isNotNull();
        assertThat(circuitBreaker.name()).isEqualTo("nameAggregation");
        assertThat(circuitBreaker.fallbackMethod()).isEqualTo("downgradeNameAggregation");
    }

    @Test
    void downgradeNameAggregationPersistsRequestForRecovery() throws Exception {
        Path recoveryFile = tempDir.resolve("name-aggregation-recovery.log");
        NameAggregationServiceImpl service = new NameAggregationServiceImpl(
                RestClient.create(),
                "http://localhost:9999/name/aggregation",
                new NameAggregationRecoveryService(recoveryFile)
        );

        NameAggregationRequest response = service.downgradeNameAggregation(
                List.of("Jessica"),
                new RuntimeException("service unavailable")
        );

        assertThat(Files.readString(recoveryFile))
                .contains("names=[Jessica]")
                .contains("reason=service unavailable");
        assertThat(response.name()).containsExactly("Jessica");
    }
}
