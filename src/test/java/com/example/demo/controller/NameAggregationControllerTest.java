package com.example.demo.controller;

import com.example.demo.dto.NameAggregationRequest;
import com.example.demo.service.NameAggregationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NameAggregationController.class)
class NameAggregationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NameAggregationService nameAggregationService;

    @Test
    void aggregateUsesTrimmedNameAndReturnsServiceResponse() throws Exception {
        when(nameAggregationService.forwardToNext(eq(List.of("Jessica"))))
                .thenReturn(new NameAggregationRequest(List.of("Jessica")));

        mockMvc.perform(post("/v1/name/aggregation").param("name", " Jessica "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name[0]").value("Jessica"));

        verify(nameAggregationService).forwardToNext(List.of("Jessica"));
    }

    @Test
    void aggregateUsesDefaultNameWhenRequestParamIsMissing() throws Exception {
        when(nameAggregationService.forwardToNext(anyList()))
                .thenReturn(new NameAggregationRequest(List.of("Jessica")));

        mockMvc.perform(post("/v1/name/aggregation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name[0]").value("Jessica"));

        verify(nameAggregationService).forwardToNext(List.of("Jessica"));
    }

    @Test
    void aggregateReturnsBadRequestWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/v1/name/aggregation").param("name", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Name must not be blank"));

        verifyNoInteractions(nameAggregationService);
    }
}
