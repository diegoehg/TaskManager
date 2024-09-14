# TaskManager
It's a task manager, the goal is to test a code synthesis tool

# Build project

## Initialization script
This project includes a initialization script on the root. This script builds the backend and frontend images;
then it creates database user used by backend.

Run it using:
```bash
./init.sh
```

## Running the project
After initializing the project, you can run it using Docker Compose:
```bash
docker-compose up --detach
```
The `--detach` option is for running it on the background. When running, the backend and frontend are exposed through
the following URLs:
- backend: http://localhost:9090
- frontend: http://localhost:3000


# Project Layers

## Backend
The backend consists of a Dockerized Spring Boot application. For building the project you can use Docker for building
the image:
```bash
docker build -t task-manager-backend:latest .
```

The steps included in the Dockerfile are:
- Building this' app fat jar using Gradle: `./gradlew clean bootJar`
- Copying into the final image
- Running the fat jar

## Frontend
The frontend consists of a Dockerized React application. This application uses parcel for building and running
the app. The following commands are needed:
```bash
npm install
npm run start
```
