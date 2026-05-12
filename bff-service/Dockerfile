FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Kontrollera filnamnet i din bff-service/target mapp
COPY target/bff-service-1.0-SNAPSHOT.jar app.jar
# BFF/Gateway är systemets entrépunkt
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]