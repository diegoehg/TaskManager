#!/bin/bash

# Navigate to the backend directory
cd backend

# Build the Spring Boot JAR
./gradlew clean bootJar

# Return to the root directory
cd ..

# Start the Docker containers
docker-compose up -d

echo "Project initialized. Backend built and Docker containers started."