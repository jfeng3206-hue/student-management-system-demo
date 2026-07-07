package com.example.demo.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentDtoTest {

    @Test
    void allArgsConstructorAndAccessorsWork() {
        StudentDto dto = new StudentDto(1L, "Jane", "Doe", "jane@example.com");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Jane");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        StudentDto dto = new StudentDto();

        dto.setId(2L);
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setEmail("john@example.com");

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Smith");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }
}
