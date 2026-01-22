# Utilise Java 21 minimal
FROM eclipse-temurin:21-jre

# Répertoire de travail dans le container
WORKDIR /app

# Copie ton JAR dedans
COPY target/befintax-0.0.1-SNAPSHOT.jar app.jar

# Expose le port de Spring Boot
EXPOSE 8080

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]