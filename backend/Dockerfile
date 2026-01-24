FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Installer Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copier le dossier backend
# COPY backend/ ./backend/
COPY . ./

WORKDIR /app/backend

# Build
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
