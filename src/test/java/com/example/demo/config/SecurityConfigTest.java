package com.example.demo.config;

import com.example.demo.dto.NameAggregationRequest;
import com.example.demo.dto.StudentDto;
import com.example.demo.controller.NameAggregationController;
import com.example.demo.controller.StudentController;
import com.example.demo.service.NameAggregationService;
import com.example.demo.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        StudentController.class,
        NameAggregationController.class
})
@Import({SecurityConfig.class, SecurityConfigTest.SecurityTestBeans.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FakeStudentService studentService;

    @Autowired
    private FakeNameAggregationService nameAggregationService;

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void unauthenticatedStudentRequestRedirectsToGoogleLogin() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/oauth2/authorization/google"));
    }

    @Test
    void oauthUserCanReadStudents() throws Exception {
        mockMvc.perform(get("/api/students")
                        .with(oauth2Login().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("jane@example.com"));
    }

    @Test
    void oauthUserCannotCreateStudent() throws Exception {
        mockMvc.perform(post("/api/students")
                        .with(oauth2Login())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "email": "jane@example.com"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateStudent() throws Exception {
        mockMvc.perform(post("/api/students")
                        .with(oauth2Login().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "email": "jane@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void oauthUserCannotDeleteStudent() throws Exception {
        mockMvc.perform(delete("/api/students/{id}", 1L).with(oauth2Login()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeleteStudent() throws Exception {
        mockMvc.perform(delete("/api/students/{id}", 1L)
                        .with(oauth2Login().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk());

        assertThat(studentService.deletedIds).containsExactly(1L);
    }

    @Test
    void oauthUserCanUseNameAggregation() throws Exception {
        mockMvc.perform(post("/v1/name/aggregation")
                        .with(oauth2Login().authorities(() -> "ROLE_USER"))
                        .param("name", "Jessica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name[0]").value("Jessica"));

        assertThat(nameAggregationService.forwardedNames).containsExactly(List.of("Jessica"));
    }

    @TestConfiguration
    static class SecurityTestBeans {

        @Bean
        @Primary
        FakeStudentService fakeStudentService() {
            return new FakeStudentService();
        }

        @Bean
        @Primary
        FakeNameAggregationService fakeNameAggregationService() {
            return new FakeNameAggregationService();
        }

        @Bean
        HealthController healthController() {
            return new HealthController();
        }
    }

    @RestController
    public static class HealthController {

        @GetMapping("/actuator/health")
        public Map<String, String> health() {
            return Map.of("status", "UP");
        }
    }

    static class FakeStudentService implements StudentService {
        private final List<Long> deletedIds = new ArrayList<>();

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
            return List.of(new StudentDto(1L, "Jane", "Doe", "jane@example.com"));
        }

        @Override
        public StudentDto updateStudent(Long id, StudentDto studentDto) {
            return new StudentDto(id, studentDto.getFirstName(), studentDto.getLastName(), studentDto.getEmail());
        }

        @Override
        public void deleteStudent(Long id) {
            deletedIds.add(id);
        }
    }

    static class FakeNameAggregationService implements NameAggregationService {
        private final List<List<String>> forwardedNames = new ArrayList<>();

        @Override
        public NameAggregationRequest forwardToNext(List<String> names) {
            forwardedNames.add(names);
            return new NameAggregationRequest(names);
        }
    }
}
