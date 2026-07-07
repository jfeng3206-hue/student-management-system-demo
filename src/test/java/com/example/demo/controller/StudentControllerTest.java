package com.example.demo.controller;

import com.example.demo.dto.StudentDto;
import com.example.demo.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = StudentController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                ServletWebSecurityAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {StudentController.class, StudentControllerTest.StudentControllerTestBeans.class})
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FakeStudentService studentService;

    @BeforeEach
    void setUp() {
        studentService.reset();
    }

    @Test
    void createStudentReturnsCreatedStudent() throws Exception {
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
        mockMvc.perform(get("/api/students/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void getAllStudentsReturnsList() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
    }

    @Test
    void updateStudentReturnsUpdatedStudent() throws Exception {
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

        org.assertj.core.api.Assertions.assertThat(studentService.deletedIds).containsExactly(1L);
    }

    @TestConfiguration
    static class StudentControllerTestBeans {

        @Bean
        @Primary
        FakeStudentService fakeStudentService() {
            return new FakeStudentService();
        }
    }

    static class FakeStudentService implements StudentService {
        private final List<Long> deletedIds = new ArrayList<>();

        void reset() {
            deletedIds.clear();
        }

        @Override
        public StudentDto createStudent(StudentDto studentDto) {
            return new StudentDto(1L, studentDto.getFirstName(), studentDto.getLastName(), studentDto.getEmail());
        }

        @Override
        public StudentDto getStudentById(Long id) {
            return new StudentDto(id, "Jane", "Doe", "jane@example.com");
        }

        @Override
        public List<StudentDto> getAllStudents() {
            return List.of(
                    new StudentDto(1L, "Jane", "Doe", "jane@example.com"),
                    new StudentDto(2L, "John", "Smith", "john@example.com")
            );
        }

        @Override
        public StudentDto updateStudent(Long id, StudentDto studentDto) {
            return new StudentDto(id, studentDto.getFirstName(), studentDto.getLastName(), studentDto.getEmail());
        }

        @Override
        public void deleteStudent(Long id) {
            deletedIds.add(Objects.requireNonNull(id));
        }
    }
}
