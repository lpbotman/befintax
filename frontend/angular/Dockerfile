# Étape 1 : builder Angular
FROM node:20-alpine AS builder
WORKDIR /app

# Copier uniquement package.json et package-lock.json pour le cache npm
COPY frontend/angular/package*.json ./

# Installer les dépendances
RUN npm install

# Copier le reste du frontend
COPY frontend/angular/ ./

# Build Angular en prod
RUN npm run build --prod

# Étape 2 : image finale Nginx
FROM nginx:alpine
COPY --from=builder /app/dist/ /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
