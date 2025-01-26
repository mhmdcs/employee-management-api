package com.example.employeemanagement.service;

import com.example.employeemanagement.entity.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    Employee createEmployee(Employee employee);

    Employee getEmployeeById(UUID id);

    Employee updateEmployee(UUID id, Employee employee);

    void deleteEmployee(UUID id);

    List<Employee> getAllEmployees();
}
