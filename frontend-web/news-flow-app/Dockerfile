# Step 1: Build the Angular app
FROM node:18 AS build

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json to install dependencies
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application
COPY . .

# Build the Angular app for production
RUN npm run build -- --configuration=production

# Step 2: Serve the app with Nginx
FROM nginx:alpine

# Copy the build output to the Nginx html folder
COPY --from=build /app/dist/news-flow-app /usr/share/nginx/html

# Copy the custom Nginx config
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start Nginx to serve the app
CMD ["nginx", "-g", "daemon off;"]
