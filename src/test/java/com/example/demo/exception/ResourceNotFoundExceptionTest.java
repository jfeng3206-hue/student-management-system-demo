package com.example.demo.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void storesMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Student not found with id: 1");

        assertThat(exception).hasMessage("Student not found with id: 1");
    }
}
