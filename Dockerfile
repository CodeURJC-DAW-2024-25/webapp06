# Use an official OpenJDK runtime as a base image
FROM maven:3.9-eclipse-temurin-21 AS builder


# Set working directory
WORKDIR /app

COPY backend/pom.xml /app/

COPY backend/src /app/src


RUN mvn clean package -DskipTests

#-------------------------------------------------------

# Use a smaller JRE image for running the app
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8443

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
