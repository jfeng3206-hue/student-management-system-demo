package com.example.demo.service.impl;

import com.example.demo.exception.NameAggregationForwardingException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Aspect
@Component
// Handles persistence exceptions as cross-cutting behavior.
public class NameAggregationRecoveryAspect {

    @AfterThrowing(
            pointcut = "execution(* com.example.demo.service.impl.NameAggregationRecoveryService.persist(..))",
            throwing = "ex"
    )
    public void translateRecoveryPersistenceFailure(IOException ex) {
        throw new NameAggregationForwardingException(
                "Failed to persist name aggregation request for recovery",
                ex
        );
    }
}
