package com.example.demo.exception;

public class InvalidNameAggregationRequestException extends RuntimeException {

    public InvalidNameAggregationRequestException(String message) {
        super(message);
    }
}
