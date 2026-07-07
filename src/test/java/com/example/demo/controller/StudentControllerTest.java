package com.example.demo.controller;

import com.example.demo.dto.StudentDto;
import com.example.demo.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    void createStudentReturnsCreatedStudent() throws Exception {
        StudentDto request = new StudentDto(null, "Jane", "Doe", "jane@example.com");
        StudentDto response = new StudentDto(1L, "Jane", "Doe", "jane@example.com");
        when(studentService.createStudent(any(StudentDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "email": "jane@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void getStudentByIdReturnsStudent() throws Exception {
        when(studentService.getStudentById(1L))
                .thenReturn(new StudentDto(1L, "Jane", "Doe", "jane@example.com"));

        mockMvc.perform(get("/api/students/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void getAllStudentsReturnsList() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(
                new StudentDto(1L, "Jane", "Doe", "jane@example.com"),
                new StudentDto(2L, "John", "Smith", "john@example.com")
        ));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
    }

    @Test
    void updateStudentReturnsUpdatedStudent() throws Exception {
        StudentDto request = new StudentDto(null, "Janet", "Doe", "janet@example.com");
        StudentDto response = new StudentDto(1L, "Janet", "Doe", "janet@example.com");
        when(studentService.updateStudent(any(Long.class), any(StudentDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/students/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Janet",
                                  "lastName": "Doe",
                                  "email": "janet@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Janet"));
    }

    @Test
    void deleteStudentReturnsConfirmationMessage() throws Exception {
        mockMvc.perform(delete("/api/students/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Student deleted successfully"));

        verify(studentService).deleteStudent(1L);
    }
}
