# Stage 1: Build the Spring Boot application
FROM gradle:8.14-jdk21-corretto-al2023 AS build

ARG PROJECT=/home/gradle/project

# Copy Gradle config files and source code
COPY --chown=gradle:gradle ./src ${PROJECT}/src/
COPY --chown=gradle:gradle ./build.gradle.kts ${PROJECT}/build.gradle.kts
COPY --chown=gradle:gradle ./settings.gradle.kts ${PROJECT}/settings.gradle.kts

WORKDIR ${PROJECT}

USER gradle

# Build the JAR file
RUN gradle build --no-daemon

# Stage 2: Create the final image
FROM eclipse-temurin:21.0.4_7-jre-ubi9-minimal

WORKDIR /app

# Expose port 9090
EXPOSE 9090

# Copy the JAR file from the build stage
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

# Set the entry point to run the Spring Boot JAR
ENTRYPOINT ["java", "-jar", "app.jar"]