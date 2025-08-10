# Use Eclipse Temurin JDK 21 as base image
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy build/libs/*.jar to /app/app.jar
COPY build/libs/*.jar app.jar

# Expose Micronaut default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
