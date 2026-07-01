package com.example.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @Test
    void handlesNameAggregationForwardingExceptionAsServiceUnavailable() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<Map<String, Object>> response = handler.handleNameAggregationForwardingException(
                new NameAggregationForwardingException("Failed to persist name aggregation request for recovery",
                        new RuntimeException("disk full"))
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody())
                .containsEntry("status", HttpStatus.SERVICE_UNAVAILABLE.value())
                .containsEntry("error", "Service Unavailable")
                .containsEntry("message", "Failed to persist name aggregation request for recovery");
    }
}
