package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.exception.InvalidInputException;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.DepartmentValidatorService;
import com.example.employeemanagement.service.EmailValidatorService;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.service.NotificationService;
import com.example.employeemanagement.util.AuditLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmailValidatorService emailValidatorService;
    private final DepartmentValidatorService departmentValidatorService;
    private final NotificationService notificationService;
    private final AuditLogger auditLogger;

    @Override
    public Employee createEmployee(Employee employee) {
        auditLogger.log("Starting creation of employee with email: " + employee.getEmail());

        // validate email with third-party
        boolean emailValid = emailValidatorService.validateEmail(employee.getEmail());
        if (!emailValid) {
            throw new InvalidInputException("Email is invalid according to third-party validation");
        }

        // validate department with third-party
        boolean departmentValid = departmentValidatorService.validateDepartment(employee.getDepartment());
        if (!departmentValid) {
            throw new InvalidInputException("Department is invalid according to third-party validation");
        }

        // save employee
        Employee savedEmployee = employeeRepository.save(employee);

        auditLogger.log("Employee saved successfully: " + savedEmployee.getId());

        // send asynchronous email notification
        notificationService.sendEmployeeCreatedNotification(savedEmployee);

        auditLogger.log("Employee creation process completed for: " + savedEmployee.getId());
        return savedEmployee;
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public Employee updateEmployee(UUID id, Employee employee) {
        Employee existing = getEmployeeById(id);

        // update fields
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setEmail(employee.getEmail());
        existing.setDepartment(employee.getDepartment());
        existing.setSalary(employee.getSalary());

        // re-validate email if changed
        if (!existing.getEmail().equals(employee.getEmail())) {
            if (!emailValidatorService.validateEmail(employee.getEmail())) {
                throw new InvalidInputException("Updated email is invalid according to third-party validation");
            }
        }
        // re-validate department if changed
        if (!existing.getDepartment().equals(employee.getDepartment())) {
            if (!departmentValidatorService.validateDepartment(employee.getDepartment())) {
                throw new InvalidInputException("Updated department is invalid according to third-party validation");
            }
        }

        return employeeRepository.save(existing);
    }

    @Override
    public void deleteEmployee(UUID id) {
        Employee existing = getEmployeeById(id);
        employeeRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
