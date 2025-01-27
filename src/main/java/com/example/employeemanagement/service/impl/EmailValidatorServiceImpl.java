package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.exception.ThirdPartyApiException;
import com.example.employeemanagement.service.EmailValidatorService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class EmailValidatorServiceImpl implements EmailValidatorService {

    @Value("${thirdparty.email.validation.url}")
    private String emailValidationApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @CircuitBreaker(name = "emailValidatorService", fallbackMethod = "emailFallback")
    public boolean validateEmail(String email) {
        try {
            // simulate a third-party call
            // in prod scenario, we'd pass the email in a request param or request body, etc
            String url = emailValidationApiUrl + "?email=" + email;

            log.info("Calling third-party email validation API at: {}", url);

            // mock response: we just simulate a JSON response like {"isValid": true}
            // for demonstration purposes, we can assume it always returns true
            // or parse an actual JSON if the real service does
            // so for now we use a dummy check:
            boolean isValid = true;

            // (in prod code) maybe we prob do:
            // ResponseEntity<EmailValidationResponse> response = restTemplate.getForEntity(url, EmailValidationResponse.class);
            // boolean isValid = response.getBody().isValid();

            // for demonstration, we'll randomly throw an exception for circuit breaker demonstration
            // double random = Math.random();
            // if(random < 0.2) {
            //     throw new RuntimeException("Simulated random third-party exception");
            // }

            return isValid;
        } catch (Exception e) {
            log.error("Error calling third-party email validation service: {}", e.getMessage());
            throw new ThirdPartyApiException("Failed to validate email due to third-party API error");
        }
    }

    // fallback method if circuit breaker is OPEN or fails
    public boolean emailFallback(String email, Throwable throwable) {
        log.warn("Circuit breaker triggered for email validation. Using fallback for email: {}", email);
        // decide whether to accept or reject email when the 3rd party is down
        // for safety, let's consider all emails invalid if we can't validate them.
        return false;
    }
}
