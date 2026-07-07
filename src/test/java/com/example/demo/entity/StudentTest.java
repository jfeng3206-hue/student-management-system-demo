package com.example.demo.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StudentTest {

    @Test
    void constructorsAndAccessorsWork() {
        Department department = new Department();
        Student student = new Student(1L, "Jane", "Doe", "jane@example.com", List.of(department));

        assertThat(student.getId()).isEqualTo(1L);
        assertThat(student.getFirstName()).isEqualTo("Jane");
        assertThat(student.getLastName()).isEqualTo("Doe");
        assertThat(student.getEmail()).isEqualTo("jane@example.com");
        assertThat(student.getDepartments()).containsExactly(department);
    }

    @Test
    void settersWork() {
        Student student = new Student();
        Department department = new Department();

        student.setId(2L);
        student.setFirstName("John");
        student.setLastName("Smith");
        student.setEmail("john@example.com");
        student.setDepartments(List.of(department));

        assertThat(student.getId()).isEqualTo(2L);
        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getLastName()).isEqualTo("Smith");
        assertThat(student.getEmail()).isEqualTo("john@example.com");
        assertThat(student.getDepartments()).containsExactly(department);
    }
}
