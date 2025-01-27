FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/employee-management-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]

# to build and run:
# mvn clean package
# docker build -t employee-management-api:1.0.0 .
# docker run -p 8080:8080 employee-management-api:1.0.0

# to kill the server process:
# lsof -i :8080
# kill PID