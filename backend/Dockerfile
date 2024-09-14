# Stage 1: Build the Spring Boot application
FROM eclipse-temurin:21.0.4_7-jdk AS build

WORKDIR /build

# Copy Gradle files and source code
COPY gradle /build/gradle
COPY src /build/src
COPY build.gradle.kts /build/
COPY gradlew /build/
COPY settings.gradle.kts /build/

# Build the JAR file
RUN ./gradlew clean bootJar

# Stage 2: Create the final image
FROM eclipse-temurin:21.0.4_7-jre-ubi9-minimal

WORKDIR /app

# Expose port 9090
EXPOSE 9090

# Copy the JAR file from the build stage
COPY --from=build /build/build/libs/*.jar app.jar

# Set the entry point to run the Spring Boot JAR
ENTRYPOINT ["java", "-jar", "app.jar"]