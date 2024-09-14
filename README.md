# TaskManager
It's a task manager, the goal is to test a code synthesis tool

# Build project

## Initialization script
This project includes a initialization script on the root. This script builds the backend bootable jar and then
runs the frontend and backend containers using docker-compose. Run it using:
```bash
./init.sh
```

## Backend
The backend consists of a Dockerized Spring Boot application. At this moment, for building the project,
you need to generate a fat jar for this app, and then build the Docker image:
```bash
cd backend/
./gradlew clean # just in case a previous build was running
./gradlew bootJar # generates fat jar
docker build -t task-manager-backend:latest .
```

These steps are included in the Dockerfile.

## Frontend
The frontend consists of a Dockerized React application. This application uses parcel for building and running
the app. The following commands are needed:
```bash
npm install
npm run start
```
