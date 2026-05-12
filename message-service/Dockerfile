FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Kontrollera att filnamnet i target/ matchar exakt det som står här nedan
COPY target/message-service-1.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]