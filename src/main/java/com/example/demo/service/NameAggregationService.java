package com.example.demo.service;

import com.example.demo.dto.NameAggregationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class NameAggregationService {

    private static final Logger log = LoggerFactory.getLogger(NameAggregationService.class);
    private static final String NEXT_SERVICE_URL = "http://18.216.74.156:8080/name/aggregation";

    private final RestClient restClient = RestClient.create();

    @Async
    public void forwardToNext(List<String> names) {
        try {
            NameAggregationRequest body = new NameAggregationRequest(names);
            NameAggregationRequest response = restClient.post()
                    .uri(NEXT_SERVICE_URL)
                    .body(body)
                    .retrieve()
                    .body(NameAggregationRequest.class);
            log.info("Response from next service: {}", response);
        } catch (Exception e) {
            log.error("Failed to forward to next service: {}", e.getMessage());
        }
    }
}
