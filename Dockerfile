FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/employee-management-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]

# to build and run:
# mvn clean package
# docker build -t employee-management-api:1.0.0 .
# docker run -p 8080:8080 employee-management-api:1.0.0

# to kill the server process:
# lsof -i :8080
# kill PID