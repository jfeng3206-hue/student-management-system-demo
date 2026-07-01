package com.example.demo.service.impl;

import com.example.demo.dto.NameAggregationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

@Service
// Stores failed requests.
public class NameAggregationRecoveryService {

    private final Path recoveryFile;

    public NameAggregationRecoveryService(
            @Value("${name-aggregation.recovery-file:target/name-aggregation-recovery.log}") String recoveryFile
    ) {
        this(Path.of(recoveryFile));
    }

    NameAggregationRecoveryService(Path recoveryFile) {
        this.recoveryFile = recoveryFile;
    }

    public void persist(NameAggregationRequest body, Throwable cause) throws IOException {
        Path parent = recoveryFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        String entry = "%s names=%s reason=%s%n".formatted(Instant.now(), body.name(), cause.getMessage());
        Files.writeString(recoveryFile, entry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
