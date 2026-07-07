package com.example.demo.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NameAggregationRequestTest {

    @Test
    void recordStoresNamesInOrder() {
        NameAggregationRequest request = new NameAggregationRequest(List.of("Jessica", "Jane"));

        assertThat(request.name()).containsExactly("Jessica", "Jane");
    }
}
