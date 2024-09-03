# Use a Node.js image as the base
FROM node:20.17.0-alpine3.19

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json (if it exists)
COPY ./package*.json ./

# Clear npm cache
RUN npm cache clean --force

# Install dependencies
RUN npm install

# Copy the rest of the frontend application code
COPY ./index.* .

# Build the React application
RUN npm run build

# Expose the port the React app will run on
EXPOSE 3000

# Start the React app
CMD ["npm", "run", "start"]