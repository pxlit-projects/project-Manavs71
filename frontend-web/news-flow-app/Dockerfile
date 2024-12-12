# Use the official Node.js image
FROM node:18-alpine as build

# Set working directory
WORKDIR /app

# Copy the package.json and install dependencies
COPY package.json package-lock.json ./
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the Angular app
RUN npm run build --prod

# Use the official Nginx image to serve the app
FROM nginx:alpine

# Copy the build output from the build stage
COPY --from=build /app/dist/my-post-app /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
