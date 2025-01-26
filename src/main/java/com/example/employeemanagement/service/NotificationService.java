package com.example.employeemanagement.service;

import com.example.employeemanagement.entity.Employee;

public interface NotificationService {
    void sendEmployeeCreatedNotification(Employee employee);
}
