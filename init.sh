#!/bin/bash

echo "Builds whole project"
docker-compose up --build --detach

echo "Waits for MongoDB to start"
sleep 10

echo "Connects to MongoDB and creates new user"
docker-compose exec mongodb mongosh --username root --password example \
  --eval 'use admin' \
  --eval 'const username = "taskManagerUser"' \
  --eval 'const existingUser = db.system.users.findOne({ user: username })' \
  --eval 'if (existingUser) db.dropUser(username)' \
  --eval 'db.createUser({
            user: username,
            pwd: "TaskUser847584",
            roles: [
              { role: "readWrite", db: "admin" },
              { role: "readWrite", db: "task-manager-db" },
            ],
          })'

echo "Project initialized."