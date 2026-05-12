FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Kontrollera filnamnet i din bot-service/target mapp
COPY target/bot-service-1.0-SNAPSHOT.jar app.jar
# Boten behöver oftast inte exponera en publik port,
# men vi kan ange 8084 om du har en controller där.
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]