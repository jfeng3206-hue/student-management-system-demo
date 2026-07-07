package com.example.demo.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidNameAggregationRequestExceptionTest {

    @Test
    void storesMessage() {
        InvalidNameAggregationRequestException exception =
                new InvalidNameAggregationRequestException("Name must not be blank");

        assertThat(exception).hasMessage("Name must not be blank");
    }
}
