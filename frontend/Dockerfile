# Use a Node.js image as the base
FROM node:22-alpine3.19

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json (if it exists)
#COPY ./package*.json ./

# Install dependencies
# RUN npm install

# Copy the rest of the frontend application code
# COPY ./ .

# Build the React application
# RUN npm run build

# Expose the port the React app will run on
EXPOSE 3000

# Start the React app
CMD ["npm", "run", "start"]