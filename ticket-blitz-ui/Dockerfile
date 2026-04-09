FROM node:22-alpine AS build
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci

# Copy source code and build
COPY . .
RUN npm run build

# Stage 2: Serve the static app with Nginx
FROM nginx:alpine

# Copy the built static files from Stage 1
COPY --from=build /app/dist/ticket-blitz-ui/browser /usr/share/nginx/html

# Configure Nginx to support Angular Single Page App routing and listen on port 4000
RUN echo 'server { \
    listen 4000; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 4000

CMD ["nginx", "-g", "daemon off;"]