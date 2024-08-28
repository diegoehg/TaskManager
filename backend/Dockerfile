# Use an OpenJDK 21 image as the base
FROM eclipse-temurin:21.0.4_7-jre-ubi9-minimal

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file built from your Spring Boot project
COPY ./build/libs/*.jar app.jar

# Expose port 9090 for the Spring Boot application
EXPOSE 9090

# Set the entry point to run the Spring Boot JAR
ENTRYPOINT ["java", "-jar", "app.jar"]