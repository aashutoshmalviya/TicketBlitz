FROM node:22-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/*

COPY --from=build /app/dist/ticket-blitz-ui/browser /usr/share/nginx/html

# THE FIX: Add the location /api/ block to proxy requests to the backend
RUN echo 'server { \
    listen 4000; \
    \
    location / { \
        root /usr/share/nginx/html; \
        index index.csr.html index.html; \
        try_files $uri $uri/ /index.csr.html; \
    } \
    \
    location /api/ { \
        proxy_pass http://ticketblitz-gateway:8080; \
        proxy_set_header Host $host; \
        proxy_set_header X-Real-IP $remote_addr; \
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 4000

CMD ["nginx", "-g", "daemon off;"]