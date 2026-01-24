FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Installer Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# On copie le contenu du dossier backend (déjà ciblé par GitHub) vers /app
COPY . ./

# Build : le pom.xml est maintenant à la racine de /app
RUN mvn clean package -DskipTests

# --- ÉTAPE FINALE ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# CORRECTION ICI : Le JAR est dans /app/target/ (plus de sous-dossier backend)
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
