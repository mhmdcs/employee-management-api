package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.exception.ThirdPartyApiException;
import com.example.employeemanagement.service.DepartmentValidatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DepartmentValidatorServiceImpl implements DepartmentValidatorService {

    @Value("${thirdparty.department.validation.url}")
    private String departmentValidationApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean validateDepartment(String department) {
        try {
            // simulated call to a 3rd party
            String url = departmentValidationApiUrl + "?department=" + department;
            log.info("Calling third-party department validation API at: {}", url);

            // assume always valid for demonstration purposes :)
            boolean isValid = true;

            // in prod code we'd prob do:
            // ResponseEntity<DepartmentValidationResponse> response = restTemplate.getForEntity(url, DepartmentValidationResponse.class);
            // boolean isValid = response.getBody().isValid();

            return isValid;
        } catch (Exception e) {
            log.error("Error calling department validation service: {}", e.getMessage());
            throw new ThirdPartyApiException("Failed to validate department due to third-party API error");
        }
    }
}
