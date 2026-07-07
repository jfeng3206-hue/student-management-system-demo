package com.example.demo.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NameAggregationForwardingExceptionTest {

    @Test
    void storesMessageAndCause() {
        RuntimeException cause = new RuntimeException("disk full");
        NameAggregationForwardingException exception =
                new NameAggregationForwardingException("Failed to persist", cause);

        assertThat(exception)
                .hasMessage("Failed to persist")
                .hasCause(cause);
    }
}
