FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Kopierar JAR-filen. Kontrollera att namnet stämmer med din pom.xml
COPY target/user-service-1.0-SNAPSHOT.jar app.jar
# User Service körs på 8081 och gRPC på 9090
EXPOSE 8081
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]