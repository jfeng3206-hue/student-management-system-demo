package com.example.demo.service.impl;

import com.example.demo.dto.StudentDto;
import com.example.demo.entity.Student;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void createStudentSavesEntityAndReturnsDto() {
        StudentDto input = new StudentDto(null, "Jane", "Doe", "jane@example.com");
        Student saved = new Student(1L, "Jane", "Doe", "jane@example.com", null);
        when(studentRepository.save(any(Student.class))).thenReturn(saved);

        StudentDto result = studentService.createStudent(input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Jane");
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        assertThat(studentCaptor.getValue().getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void getStudentByIdReturnsStudentWhenFound() {
        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(new Student(1L, "Jane", "Doe", "jane@example.com", null)));

        StudentDto result = studentService.getStudentById(1L);

        assertThat(result.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void getStudentByIdThrowsResourceNotFoundWhenMissing() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Student not found with id: 99");
    }

    @Test
    void getAllStudentsReturnsMappedDtos() {
        when(studentRepository.findAll()).thenReturn(List.of(
                new Student(1L, "Jane", "Doe", "jane@example.com", null),
                new Student(2L, "John", "Smith", "john@example.com", null)
        ));

        List<StudentDto> result = studentService.getAllStudents();

        assertThat(result)
                .extracting(StudentDto::getEmail)
                .containsExactly("jane@example.com", "john@example.com");
    }

    @Test
    void updateStudentUpdatesExistingEntityAndReturnsDto() {
        Student existing = new Student(1L, "Jane", "Doe", "jane@example.com", null);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        StudentDto result = studentService.updateStudent(
                1L,
                new StudentDto(null, "Janet", "Dane", "janet@example.com")
        );

        assertThat(result.getFirstName()).isEqualTo("Janet");
        assertThat(result.getLastName()).isEqualTo("Dane");
        assertThat(result.getEmail()).isEqualTo("janet@example.com");
        verify(studentRepository).save(existing);
    }

    @Test
    void updateStudentThrowsRuntimeExceptionWhenMissing() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.updateStudent(
                99L,
                new StudentDto(null, "Jane", "Doe", "jane@example.com")
        ))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found with id: 99");
        verify(studentRepository, never()).save(any());
    }

    @Test
    void deleteStudentDeletesWhenFound() {
        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(new Student(1L, "Jane", "Doe", "jane@example.com", null)));

        studentService.deleteStudent(1L);

        verify(studentRepository).deleteById(1L);
    }

    @Test
    void deleteStudentThrowsRuntimeExceptionWhenMissing() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.deleteStudent(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found with id: 99");
        verify(studentRepository, never()).deleteById(99L);
    }
}
