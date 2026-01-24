# Étape 1 : builder Angular
FROM node:20-alpine AS builder
WORKDIR /app

# On copie les fichiers de configuration depuis le dossier frontend/angular
COPY frontend/angular/package*.json ./

# Installer les dépendances
RUN npm install

# Copier tout le contenu du dossier frontend/angular
COPY frontend/angular/ ./

# Build Angular
RUN npm run build --prod

# Étape 2 : image finale Nginx
FROM nginx:alpine
RUN rm -rf /usr/share/nginx/html/*

# Copie des fichiers buildés
COPY --from=builder /app/dist/ /usr/share/nginx/html/

# ON ÉCRIT LA CONFIG DIRECTEMENT DANS LE DOCKERFILE
RUN printf "server { \n\
    listen 80; \n\
    server_name localhost; \n\
    root /usr/share/nginx/html/befintax/browser; \n\
    index index.html; \n\
    location / { \n\
        try_files \$uri \$uri/ /index.html; \n\
    } \n\
    location /api/ { \n\
        proxy_pass http://befintax-backend:8080/; \n\
        proxy_set_header Host \$host; \n\
    } \n\
}" > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
