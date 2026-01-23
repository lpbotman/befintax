# -----------------------------
# Étape 1 : Builder Maven
# -----------------------------
FROM maven:3.9.1-eclipse-temurin-21 AS builder
WORKDIR /app

# Copie tout le backend dans le container
COPY . .

# Build Maven, skip les tests pour gagner du temps
RUN mvn clean package -DskipTests

# -----------------------------
# Étape 2 : Image finale Java 21
# -----------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copie le JAR généré depuis l'étape builder
COPY --from=builder /app/target/befintax-0.0.1-SNAPSHOT.jar app.jar

# Expose le port de Spring Boot
EXPOSE 8080

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
