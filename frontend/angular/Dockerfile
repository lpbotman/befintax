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
# Le résultat du build se trouve dans dist/befintax (ou dist/)
COPY --from=builder /app/dist/ /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
