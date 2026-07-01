package com.example.demo.service.impl;

import com.example.demo.exception.NameAggregationForwardingException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameAggregationRecoveryAspectTest {

    @Test
    void translatesIOExceptionToNameAggregationForwardingException() throws Throwable {
        NameAggregationRecoveryAspect aspect = new NameAggregationRecoveryAspect();

        assertThatThrownBy(() -> aspect.translateRecoveryPersistenceFailure(new IOException("disk full")))
                .isInstanceOf(NameAggregationForwardingException.class)
                .hasMessage("Failed to persist name aggregation request for recovery")
                .hasCauseInstanceOf(IOException.class);
    }
}
