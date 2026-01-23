# -----------------------------
# Étape 1 : Builder Maven
# -----------------------------
FROM maven:3.9.2-eclipse-temurin-20 AS builder
WORKDIR /app

# Copier uniquement le dossier backend
COPY backend/ ./backend/

# Aller dans le sous-dossier backend
WORKDIR /app/backend

# Build Maven, skip tests
RUN mvn clean package -DskipTests

# -----------------------------
# Étape 2 : Image finale Java 21
# -----------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copier le JAR généré depuis l'étape builder
COPY --from=builder /app/backend/target/befintax-0.0.1-SNAPSHOT.jar app.jar

# Expose le port de Spring Boot
EXPOSE 8080

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
