package com.example.employeemanagement.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditLogger {

    public void log(String message) {
        log.info("[AUDIT] {}", message);
    }
}
