package com.example.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @Test
    void handlesResourceNotFoundExceptionAsNotFound() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFoundException(
                new ResourceNotFoundException("Student not found with id: 1")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody())
                .containsEntry("status", HttpStatus.NOT_FOUND.value())
                .containsEntry("error", "Not Found")
                .containsEntry("message", "Student not found with id: 1");
    }

    @Test
    void handlesInvalidNameAggregationRequestAsBadRequest() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<Map<String, Object>> response = handler.handleInvalidNameAggregationRequest(
                new InvalidNameAggregationRequestException("Name must not be blank")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .containsEntry("status", HttpStatus.BAD_REQUEST.value())
                .containsEntry("error", "Bad Request")
                .containsEntry("message", "Name must not be blank");
    }

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
