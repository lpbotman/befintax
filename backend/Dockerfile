# -----------------------------
# Étape 1 : Builder Maven avec Java 21
# -----------------------------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Installer Maven (Debian/Ubuntu)
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copier uniquement le backend
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
