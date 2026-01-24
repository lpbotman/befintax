# Étape 1 : builder Angular
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --prod

# Étape 2 : image finale Nginx
FROM nginx:alpine
# On vide le contenu par défaut
RUN rm -rf /usr/share/nginx/html/*
# On copie le dossier dist
COPY --from=builder /app/dist/ /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
