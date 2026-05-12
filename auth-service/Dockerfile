# Steg 1: Använd en officiell Java-image som bas
FROM eclipse-temurin:17-jdk-alpine

# Steg 2: Sätt arbetskatalogen inne i containern
WORKDIR /app

# Steg 3: Kopiera den färdigbyggda JAR-filen från din target-mapp till containern
# Se till att namnet matchar det som skapas när du kör 'mvn clean package'
COPY target/auth-service-1.0-SNAPSHOT.jar app.jar

# Steg 4: Berätta vilken port containern ska lyssna på
EXPOSE 8083

# Steg 5: Kommandot för att starta applikationen
ENTRYPOINT ["java", "-jar", "app.jar"]