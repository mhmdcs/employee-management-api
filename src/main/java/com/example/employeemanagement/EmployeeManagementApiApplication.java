package com.example.employeemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EmployeeManagementApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagementApiApplication.class, args);
	}
}
