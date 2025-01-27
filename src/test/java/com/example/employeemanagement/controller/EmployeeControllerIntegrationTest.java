package com.example.employeemanagement.controller;

import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// we can run with a random port, but here we'll just rely on MockMvc & SpringBootTest
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // optional, if you want to use a separate application-test.properties
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    void createEmployee_ShouldReturnCreatedStatusAndEmployee_WhenValidRequest() throws Exception {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .department("Engineering")
                .salary(BigDecimal.valueOf(3000))
                .build();

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void createEmployee_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        Employee employee = Employee.builder()
                .firstName("Invalid")
                .lastName("Email")
                .email("invalid-email")
                .department("Engineering")
                .salary(BigDecimal.valueOf(3000))
                .build();

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Validation failed")));
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenEmployeeExists() throws Exception {
        // pre-save an employee
        Employee saved = employeeRepository.save(Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@test.com")
                .department("HR")
                .salary(BigDecimal.valueOf(4000))
                .build());

        mockMvc.perform(get("/api/employees/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane.doe@test.com"));
    }

    @Test
    void getEmployeeById_ShouldReturnNotFound_WhenEmployeeDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Employee not found")));
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee_WhenValidRequest() throws Exception {
        Employee saved = employeeRepository.save(Employee.builder()
                .firstName("Old")
                .lastName("Name")
                .email("old.name@test.com")
                .department("Engineering")
                .salary(BigDecimal.valueOf(3500))
                .build());

        Employee updateReq = Employee.builder()
                .firstName("New")
                .lastName("Name")
                .email("new.name@test.com")
                .department("Marketing")
                .salary(BigDecimal.valueOf(4500))
                .build();

        mockMvc.perform(put("/api/employees/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.department").value("Marketing"));
    }

    @Test
    void deleteEmployee_ShouldReturnNoContent_WhenEmployeeExists() throws Exception {
        Employee saved = employeeRepository.save(Employee.builder()
                .firstName("ToBe")
                .lastName("Deleted")
                .email("delete.me@test.com")
                .department("Sales")
                .salary(BigDecimal.valueOf(2000))
                .build());

        mockMvc.perform(delete("/api/employees/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() throws Exception {
        employeeRepository.save(Employee.builder()
                .firstName("First")
                .lastName("Employee")
                .email("first.employee@test.com")
                .department("Engineering")
                .salary(BigDecimal.valueOf(5000))
                .build());

        employeeRepository.save(Employee.builder()
                .firstName("Second")
                .lastName("Employee")
                .email("second.employee@test.com")
                .department("HR")
                .salary(BigDecimal.valueOf(4000))
                .build());

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
