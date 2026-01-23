# -----------------------------
# Étape 1 : Builder Maven avec Java 21
# -----------------------------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Installer Maven
RUN microdnf install -y maven && microdnf clean all

# Copier uniquement le backend
COPY backend/ ./backend/

WORKDIR /app/backend

# Build Maven
RUN mvn clean package -DskipTests

# -----------------------------
# Étape 2 : Image finale Java 21
# -----------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/backend/target/befintax-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
