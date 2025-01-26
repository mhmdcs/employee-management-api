package com.example.employeemanagement.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4jCircuitBreakerConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        // This is a programmatic config. We can also use the properties in application.properties.
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 50% failures to open the circuit
                .slidingWindowSize(5)
                .build();
        return CircuitBreakerRegistry.of(cbConfig);
    }
}
