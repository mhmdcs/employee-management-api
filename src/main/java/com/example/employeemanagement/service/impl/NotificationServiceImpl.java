package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendEmployeeCreatedNotification(Employee employee) {
        // since we don't have an actual SMTP, we can simply just log it
        // but let's demonstrate a real SimpleMailMessage
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(employee.getEmail());
            message.setSubject("Welcome to the Company!");
            message.setText("Hello " + employee.getFirstName() + ",\n\n" +
                    "Your employee record has been created successfully.\n" +
                    "Department: " + employee.getDepartment() + "\n" +
                    "Salary: $" + employee.getSalary() + "\n\n" +
                    "Best Regards,\nCompany HR");

            mailSender.send(message);
            log.info("Email notification sent to {}", employee.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email notification to {}: {}", employee.getEmail(), e.getMessage());
        }
    }
}
