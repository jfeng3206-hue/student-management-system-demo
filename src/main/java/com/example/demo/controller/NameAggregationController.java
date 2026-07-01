package com.example.demo.controller;

import com.example.demo.dto.NameAggregationRequest;
import com.example.demo.exception.InvalidNameAggregationRequestException;
import com.example.demo.service.NameAggregationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/name")
public class NameAggregationController {

    private final NameAggregationService nameAggregationService;

    public NameAggregationController(NameAggregationService nameAggregationService) {
        this.nameAggregationService = nameAggregationService;
    }

    @PostMapping("/aggregation")
    public ResponseEntity<NameAggregationRequest> aggregate(
            @RequestParam(name = "name", defaultValue = "Jessica") String name
    ) {
        if (name == null || name.isBlank()) {
            throw new InvalidNameAggregationRequestException("Name must not be blank");
        }

        List<String> names = List.of(name.trim());

        nameAggregationService.forwardToNext(names);

        return ResponseEntity.ok(new NameAggregationRequest(names));
    }
}
