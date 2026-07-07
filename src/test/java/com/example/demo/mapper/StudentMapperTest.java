package com.example.demo.mapper;

import com.example.demo.dto.StudentDto;
import com.example.demo.entity.Department;
import com.example.demo.entity.Student;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StudentMapperTest {

    @Test
    void mapToStudentDtoMapsAllFields() {
        Student student = new Student(1L, "Jane", "Doe", "jane@example.com", List.of(new Department()));

        StudentDto dto = StudentMapper.mapToStudentDto(student);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Jane");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void mapToStudentMapsAllDtoFieldsAndLeavesDepartmentsEmpty() {
        StudentDto dto = new StudentDto(2L, "John", "Smith", "john@example.com");

        Student student = StudentMapper.mapToStudent(dto);

        assertThat(student.getId()).isEqualTo(2L);
        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getLastName()).isEqualTo("Smith");
        assertThat(student.getEmail()).isEqualTo("john@example.com");
        assertThat(student.getDepartments()).isNull();
    }
}
