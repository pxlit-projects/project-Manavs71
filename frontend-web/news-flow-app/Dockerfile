# Use the official Node.js image as the base image
FROM node:18.19

# Set the working directory
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the Angular application
RUN npm run build --prod

# Install a simple HTTP server to serve the Angular application
RUN npm install -g http-server

# Set the working directory to the build output directory
WORKDIR /app/dist/news-flow-app/browser

# Expose port 80
EXPOSE 80

# Start the HTTP server
CMD ["http-server", "-p", "80", "-c-1", "-o", "index.html"]
