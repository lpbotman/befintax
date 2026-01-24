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
# On vide le dossier par défaut pour éviter les conflits
RUN rm -rf /usr/share/nginx/html/*
# On copie les fichiers (vérifie bien que c'est /app/dist/befintax selon ton build)
COPY --from=builder /app/dist/ /usr/share/nginx/html/
# ON AJOUTE CETTE LIGNE (très important) :
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
