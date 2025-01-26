# Employee Management API

A **Spring Boot** application for creating, retrieving, updating, and deleting employee records. Demonstrates:

- REST API Development
- H2 Database Integration (using JPA)
- Validation & Exception Handling
- Logging (`@Slf4j` / custom `AuditLogger`)
- Third-Party API integrations (mock or real) for email & department validation
- Asynchronous Email Notifications
- Rate Limiting (Bucket4j) & Circuit Breaker (Resilience4j)
- Testing (JUnit + Mockito)
- OpenAPI/Swagger UI for API documentation

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
    - [Local Development](#local-development)
    - [Docker (Optional)](#docker-optional)
- [Configuration](#configuration)
- [Running the Tests](#running-the-tests)
- [API Endpoints](#api-endpoints)
- [Swagger UI / OpenAPI](#swagger-ui--openapi)
- [License](#license)

---

## Features

1. **CRUD Operations** for `Employee` entity with attributes:
    - `id` (UUID)
    - `firstName`, `lastName`
    - `email` (validated)
    - `department` (validated)
    - `salary`

2. **H2 In-Memory Database** for quick setup.
3. **Validation** (using `javax.validation` / Bean Validation).
4. **Global Exception Handling** with custom error responses (`ApiError`).
5. **Asynchronous Notification** (email sending via `Spring Mail` on employee creation).
6. **Third-Party Integrations** (mock calls for email & department validation).
7. **Rate Limiting** via Bucket4j (optional config).
8. **Circuit Breaker** via Resilience4j (optional usage on email validation).
9. **OpenAPI/Swagger** available at `/swagger-ui/index.html`.
10. **JUnit & Mockito Tests** (unit & integration).

---

## Prerequisites

- **Java 17+** installed
- **Maven 3.8+** installed
- **(Optional)** Docker (for containerized deployment)
- **(Optional)** Lombok plugin in your IDE (e.g., IntelliJ) with annotation processing enabled

---

## Getting Started

### Local Development

1. **Clone or Download** the repo.
2. **Build & Run** using Maven:
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```
   or simply run the **main** method in `EmployeeManagementApiApplication.java` from your IDE.

3. The app starts on **http://localhost:8080** by default.
4. **H2 Console** (optional) is available at **`/h2-console`** (credentials in `application.properties`).

### Docker (Optional)

1. Make sure you have a **Dockerfile** at the project root:
   ```dockerfile
   FROM eclipse-temurin:17-jdk-alpine
   VOLUME /tmp
   COPY target/employee-management-api-0.0.1-SNAPSHOT.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java","-jar","/app.jar"]
   ```
2. **Build Docker Image**:
   ```bash
   mvn clean package
   docker build -t employee-management-api:latest .
   ```
3. **Run Container**:
   ```bash
   docker run -p 8080:8080 employee-management-api:latest
   ```
4. Access the API at **`http://localhost:8080`**.

---

## Configuration

Key properties in `application.properties`:

- **Server Port**: `server.port=8080`
- **H2 Database**:
  ```properties
  spring.h2.console.enabled=true
  spring.h2.console.path=/h2-console
  spring.datasource.url=jdbc:h2:mem:employee_db
  ```
- **Spring JPA**:
  ```properties
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true
  ```
- **Third-Party Validation** (mock URLs):
  ```properties
  thirdparty.email.validation.url=https://mock-email-validator.com/api/check
  thirdparty.department.validation.url=https://mock-department-validator.com/api/check
  ```
- **Rate Limiting / Circuit Breaker** can be configured in `application.properties` or in separate config classes.

---

## Running the Tests

- **Unit Tests** (Mockito + JUnit):  
  Check `src/test/java/com/example/employeemanagement/service/EmployeeServiceTest.java` and others.
- **Integration Tests** (MockMvc + JUnit):  
  Check `src/test/java/com/example/employeemanagement/controller/EmployeeControllerIntegrationTest.java`.
- **Run** them via:
  ```bash
  mvn clean test
  ```

---

## API Endpoints

All endpoints are under **`/api/employees`**:

1. **Create Employee**
    - **POST** `/api/employees`
    - Request Body: JSON with `firstName`, `lastName`, `email`, `department`, `salary`
    - **201 Created** on success

2. **Get Employee by ID**
    - **GET** `/api/employees/{id}`
    - **200 OK** + Employee JSON
    - **404 Not Found** if invalid ID

3. **Update Employee**
    - **PUT** `/api/employees/{id}`
    - Request Body: JSON with updated fields
    - **200 OK** + updated Employee JSON, or **404 Not Found**

4. **Delete Employee**
    - **DELETE** `/api/employees/{id}`
    - **204 No Content** on success, or **404 Not Found**

5. **List All Employees**
    - **GET** `/api/employees`
    - Returns a **200 OK** + JSON array of Employees

---

## Swagger UI / OpenAPI

With **SpringDoc**:

- **OpenAPI JSON**: `GET /v3/api-docs`
- **Swagger UI**: `GET /swagger-ui/index.html`

Use the Swagger UI to test all endpoints interactively.
