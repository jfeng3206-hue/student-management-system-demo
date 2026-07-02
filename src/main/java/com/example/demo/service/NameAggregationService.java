package com.example.demo.service;

import com.example.demo.dto.NameAggregationRequest;

import java.util.List;

public interface NameAggregationService {

    NameAggregationRequest forwardToNext(List<String> names);
}
