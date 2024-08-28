# TaskManager
It's a task manager, the goal is to test a code synthesis tool

# Build project

## Backend
The backend consists of a Dockerized Spring Boot application. At this moment, for building the project,
you need to generate a fat jar for this app, and then build the Docker image:
```bash
cd backend/
./gradlew clean # just in case a previous build was running
./gradlew bootJar # generates fat jar
docker build -t task-manager-backend:latest .
```
