package com.example.demo.controller;

import com.example.demo.dto.NameAggregationRequest;
import com.example.demo.service.NameAggregationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<NameAggregationRequest> aggregate() {
        List<String> names = List.of("Jessica");

        nameAggregationService.forwardToNext(names);

        return ResponseEntity.ok(new NameAggregationRequest(names));
    }
}
