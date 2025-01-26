package com.example.employeemanagement.service;

import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.exception.InvalidInputException;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.impl.EmployeeServiceImpl;
import com.example.employeemanagement.util.AuditLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmailValidatorService emailValidatorService;

    @Mock
    private DepartmentValidatorService departmentValidatorService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee mockEmployee;
    private UUID mockId;

    @BeforeEach
    void setUp() {
        mockId = UUID.randomUUID();
        mockEmployee = Employee.builder()
                .id(mockId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department("Engineering")
                .salary(BigDecimal.valueOf(5000))
                .build();
    }

    @Test
    void createEmployee_ShouldReturnSavedEmployee_WhenValidInput() {
        // Arrange
        when(emailValidatorService.validateEmail(mockEmployee.getEmail())).thenReturn(true);
        when(departmentValidatorService.validateDepartment(mockEmployee.getDepartment())).thenReturn(true);
        when(employeeRepository.save(mockEmployee)).thenReturn(mockEmployee);

        // Act
        Employee result = employeeService.createEmployee(mockEmployee);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(mockEmployee.getId());
        verify(notificationService, times(1)).sendEmployeeCreatedNotification(mockEmployee);
        verify(auditLogger, atLeastOnce()).log(anyString());
    }

    @Test
    void createEmployee_ShouldThrowInvalidInputException_WhenEmailInvalid() {
        // Arrange
        when(emailValidatorService.validateEmail(mockEmployee.getEmail())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> employeeService.createEmployee(mockEmployee))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Email is invalid");
    }

    @Test
    void createEmployee_ShouldThrowInvalidInputException_WhenDepartmentInvalid() {
        // Arrange
        when(emailValidatorService.validateEmail(mockEmployee.getEmail())).thenReturn(true);
        when(departmentValidatorService.validateDepartment(mockEmployee.getDepartment())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> employeeService.createEmployee(mockEmployee))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Department is invalid");
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenFound() {
        // Arrange
        when(employeeRepository.findById(mockId)).thenReturn(Optional.of(mockEmployee));

        // Act
        Employee result = employeeService.getEmployeeById(mockId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(mockId);
    }

    @Test
    void getEmployeeById_ShouldThrowEmployeeNotFoundException_WhenNotFound() {
        // Arrange
        when(employeeRepository.findById(mockId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(mockId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("Employee not found");
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee_WhenValid() {
        // Arrange
        Employee updatedEmployee = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .department("Marketing")
                .salary(BigDecimal.valueOf(6000))
                .build();

        when(employeeRepository.findById(mockId)).thenReturn(Optional.of(mockEmployee));
        when(emailValidatorService.validateEmail(updatedEmployee.getEmail())).thenReturn(true);
        when(departmentValidatorService.validateDepartment(updatedEmployee.getDepartment())).thenReturn(true);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Employee result = employeeService.updateEmployee(mockId, updatedEmployee);

        // Assert
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getDepartment()).isEqualTo("Marketing");
    }

    @Test
    void deleteEmployee_ShouldNotThrow_WhenEmployeeExists() {
        // Arrange
        when(employeeRepository.findById(mockId)).thenReturn(Optional.of(mockEmployee));
        doNothing().when(employeeRepository).delete(mockEmployee);

        // Act
        employeeService.deleteEmployee(mockId);

        // Assert
        verify(employeeRepository, times(1)).delete(mockEmployee);
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() {
        // Arrange
        List<Employee> employees = Collections.singletonList(mockEmployee);
        when(employeeRepository.findAll()).thenReturn(employees);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertThat(result).hasSize(1);
    }
}
