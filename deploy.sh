#!/bin/bash
set -e  # stop si erreur

echo "🚀 Deploy Befintax"

# -------- FRONTEND --------
echo "📦 Building Angular frontend..."
cd frontend/angular
npm install
npm run build
cd ../../

# -------- BACKEND --------
echo "☕ Building Spring Boot backend..."
cd backend
./mvnw clean package -DskipTests
cd ..

# -------- DOCKER --------
echo "🐳 Building Docker images..."
docker compose build

echo "▶️ Starting containers..."
docker compose up -d

echo "✅ Deploy finished!"
