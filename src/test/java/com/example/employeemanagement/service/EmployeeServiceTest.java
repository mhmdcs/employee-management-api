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
        // arrange
        when(emailValidatorService.validateEmail(mockEmployee.getEmail())).thenReturn(true);
        when(departmentValidatorService.validateDepartment(mockEmployee.getDepartment())).thenReturn(true);
        when(employeeRepository.save(mockEmployee)).thenReturn(mockEmployee);

        // act
        Employee result = employeeService.createEmployee(mockEmployee);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(mockEmployee.getId());
        verify(notificationService, times(1)).sendEmployeeCreatedNotification(mockEmployee);
        verify(auditLogger, atLeastOnce()).log(anyString());
    }

    @Test
    void createEmployee_ShouldThrowInvalidInputException_WhenEmailInvalid() {
        // arrange
        when(emailValidatorService.validateEmail(mockEmployee.getEmail())).thenReturn(false);

        // act & assert
        assertThatThrownBy(() -> employeeService.createEmployee(mockEmployee))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Email is invalid");
    }

    @Test
    void createEmployee_ShouldThrowInvalidInputException_WhenDepartmentInvalid() {
        // arrange
        when(emailValidatorService.validateEmail(mockEmployee.getEmail())).thenReturn(true);
        when(departmentValidatorService.validateDepartment(mockEmployee.getDepartment())).thenReturn(false);

        // act & assert
        assertThatThrownBy(() -> employeeService.createEmployee(mockEmployee))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Department is invalid");
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenFound() {
        // arrange
        when(employeeRepository.findById(mockId)).thenReturn(Optional.of(mockEmployee));

        // act
        Employee result = employeeService.getEmployeeById(mockId);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(mockId);
    }

    @Test
    void getEmployeeById_ShouldThrowEmployeeNotFoundException_WhenNotFound() {
        // arrange
        when(employeeRepository.findById(mockId)).thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(mockId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("Employee not found");
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee_WhenValid() {
        // arrange
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

        // act
        Employee result = employeeService.updateEmployee(mockId, updatedEmployee);

        // assert
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getDepartment()).isEqualTo("Marketing");
    }

    @Test
    void deleteEmployee_ShouldNotThrow_WhenEmployeeExists() {
        // arrange
        when(employeeRepository.findById(mockId)).thenReturn(Optional.of(mockEmployee));
        doNothing().when(employeeRepository).delete(mockEmployee);

        // act
        employeeService.deleteEmployee(mockId);

        // assert
        verify(employeeRepository, times(1)).delete(mockEmployee);
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() {
        // arrange
        List<Employee> employees = Collections.singletonList(mockEmployee);
        when(employeeRepository.findAll()).thenReturn(employees);

        // act
        List<Employee> result = employeeService.getAllEmployees();

        // assert
        assertThat(result).hasSize(1);
    }
}
