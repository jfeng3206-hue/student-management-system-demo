package com.example.demo.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DepartmentTest {

    @Test
    void constructorsAndAccessorsWork() {
        Student student = new Student();
        Department department = new Department(1L, "Engineering", List.of(student));

        assertThat(department.getId()).isEqualTo(1L);
        assertThat(department.getName()).isEqualTo("Engineering");
        assertThat(department.getStudents()).containsExactly(student);
    }

    @Test
    void settersWork() {
        Department department = new Department();
        Student student = new Student();

        department.setId(2L);
        department.setName("Math");
        department.setStudents(List.of(student));

        assertThat(department.getId()).isEqualTo(2L);
        assertThat(department.getName()).isEqualTo("Math");
        assertThat(department.getStudents()).containsExactly(student);
    }
}
