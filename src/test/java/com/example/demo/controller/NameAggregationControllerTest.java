package com.example.demo.controller;

import com.example.demo.dto.NameAggregationRequest;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.service.NameAggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = NameAggregationController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                ServletWebSecurityAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {
        NameAggregationController.class,
        GlobalExceptionHandler.class,
        NameAggregationControllerTest.NameAggregationControllerTestBeans.class
})
class NameAggregationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FakeNameAggregationService nameAggregationService;

    @BeforeEach
    void setUp() {
        nameAggregationService.reset();
    }

    @Test
    void aggregateUsesTrimmedNameAndReturnsServiceResponse() throws Exception {
        mockMvc.perform(post("/v1/name/aggregation").param("name", " Jessica "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name[0]").value("Jessica"));

        assertThat(nameAggregationService.forwardedNames).containsExactly(List.of("Jessica"));
    }

    @Test
    void aggregateUsesDefaultNameWhenRequestParamIsMissing() throws Exception {
        mockMvc.perform(post("/v1/name/aggregation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name[0]").value("Jessica"));

        assertThat(nameAggregationService.forwardedNames).containsExactly(List.of("Jessica"));
    }

    @Test
    void aggregateReturnsBadRequestWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/v1/name/aggregation").param("name", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Name must not be blank"));

        assertThat(nameAggregationService.forwardedNames).isEmpty();
    }

    @TestConfiguration
    static class NameAggregationControllerTestBeans {

        @Bean
        @Primary
        FakeNameAggregationService fakeNameAggregationService() {
            return new FakeNameAggregationService();
        }
    }

    static class FakeNameAggregationService implements NameAggregationService {
        private final List<List<String>> forwardedNames = new ArrayList<>();

        void reset() {
            forwardedNames.clear();
        }

        @Override
        public NameAggregationRequest forwardToNext(List<String> names) {
            forwardedNames.add(names);
            return new NameAggregationRequest(names);
        }
    }
}
