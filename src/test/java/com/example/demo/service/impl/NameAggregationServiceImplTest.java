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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void forwardToNextPostsNamesAndReturnsResponseBody() {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestBodyUriSpec requestSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        NameAggregationRecoveryService recoveryService = mock(NameAggregationRecoveryService.class);
        NameAggregationRequest downstreamResponse = new NameAggregationRequest(List.of("Jessica", "Taylor"));
        when(restClient.post()).thenReturn(requestSpec);
        when(requestSpec.uri("http://next-service/v1/name/aggregation")).thenReturn(requestSpec);
        when(requestSpec.body(eq(new NameAggregationRequest(List.of("Jessica"))))).thenReturn(requestSpec);
        when(requestSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NameAggregationRequest.class)).thenReturn(downstreamResponse);
        NameAggregationServiceImpl service = new NameAggregationServiceImpl(
                restClient,
                "http://next-service/v1/name/aggregation",
                recoveryService
        );

        NameAggregationRequest result = service.forwardToNext(List.of("Jessica"));

        assertThat(result.name()).containsExactly("Jessica", "Taylor");
        verify(requestSpec).body(new NameAggregationRequest(List.of("Jessica")));
    }
}
