package com.example.demo.service.impl;

import com.example.demo.dto.NameAggregationRequest;
import com.example.demo.service.NameAggregationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Service
public class NameAggregationServiceImpl implements NameAggregationService {

    private final RestClient restClient;
    private final String nextServiceUrl;
    private final NameAggregationRecoveryService recoveryService;

    @Autowired
    public NameAggregationServiceImpl(
            @Value("${name-aggregation.next-service-url}") String nextServiceUrl,
            NameAggregationRecoveryService recoveryService
    ) {
        this(RestClient.create(), nextServiceUrl, recoveryService);
    }

    NameAggregationServiceImpl(
            RestClient restClient,
            String nextServiceUrl,
            NameAggregationRecoveryService recoveryService
    ) {
        this.restClient = restClient;
        this.nextServiceUrl = nextServiceUrl;
        this.recoveryService = recoveryService;
    }

    @Override
    @Retry(name = "nameAggregation")
    @CircuitBreaker(name = "nameAggregation", fallbackMethod = "downgradeNameAggregation")
    public void forwardToNext(List<String> names) {
        NameAggregationRequest body = new NameAggregationRequest(names);

        restClient.post()
                .uri(nextServiceUrl)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    void downgradeNameAggregation(List<String> names, Throwable cause) throws IOException {
        recoveryService.persist(new NameAggregationRequest(names), cause);
    }
}
