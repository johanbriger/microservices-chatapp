# Distributed Chat System

Detta projekt är en distribuerad chatt-applikation byggd med en mikrotjänstarkitektur. Systemet demonstrerar modern systemintegration genom användning av synkron kommunikation (gRPC, REST), asynkron händelsestyrd kommunikation (RabbitMQ) och säker autentisering (OAuth2 med PKCE).

## Arkitekturöversikt

Systemet består av följande tjänster:

*   **BFF Service (Backend-for-Frontend):** Fungerar som API Gateway och entrépunkt för webbläsaren. Hanterar routing, CORS-filtrering och servering av frontend (`index.html`).
*   **Auth Service:** En OAuth2-auktoriseringsserver som utfärdar JWT-tokens. Den använder gRPC för att verifiera användaruppgifter mot User Service.
*   **User Service:** Hanterar användardata i en PostgreSQL-databas. Exponerar både REST-endpoints och en gRPC-server.
*   **Message Service:** Ansvarar för chattlogik, sparar meddelanden i PostgreSQL och publicerar händelser till RabbitMQ när nya meddelanden skickas.
*   **Bot Service:** En händelsestyrd tjänst som lyssnar på RabbitMQ och svarar automatiskt på meddelanden som innehåller specifika nyckelord (t.ex. "Hej").

## Teknisk Stack

*   **Framework:** Spring Boot 3.2.4
*   **Programspråk:** Java 17
*   **Kommunikation:** 
    *   Synkron: gRPC (mellan Auth/Message och User Service), REST (mellan BFF och backend).
    *   Asynkron: RabbitMQ (Topic Exchange).
*   **Databas:** PostgreSQL (separata instanser för UserDB och MessageDB).
*   **Säkerhet:** Spring Security, OAuth2, PKCE (Proof Key for Code Exchange).
*   **Orkestrering:** Docker Compose & Kubernetes (Ingress NGINX).

## Förutsättningar

*   Java 17 eller högre
*   Maven 3.x
*   Docker Desktop (med Kubernetes aktiverat om du vill köra i kluster)

## Kom igång

### 1. Bygg projektet
Ställ dig i projektets rotmapp och kör:
```bash
mvn clean package -DskipTests
```

### 2. Kör med Docker Compose
För att starta hela systemet lokalt:
```bash
docker-compose up --build -d
```
Applikationen nås sedan på `http://localhost:8080`.

### 3. Kör i Kubernetes
Systemet är konfigurerat för att fungera med Ingress på domänen `chatapp.local`.

1.  **Bygg bilderna lokalt:**
    ```bash
    docker build -t chatapp-auth:v2 ./auth-service
    docker build -t chatapp-bff:v4 ./bff-service
    # ... osv för resterande tjänster
    ```
2.  **Uppdatera din hosts-fil:**
    Lägg till `127.0.0.1 chatapp.local` i din `C:\Windows\System32\drivers\etc\hosts`.
3.  **Applicera manifest:**
    ```bash
    kubectl apply -f k8s/
    ```
4.  **Installera Ingress Controller:**
    Se till att NGINX Ingress Controller är installerad i ditt kluster.

Applikationen nås på `http://chatapp.local`.

## Systemflöden (Inför examination)

### Inloggningsflöde (Synkront)
1. Webbläsaren (Frontend) initierar inloggning via BFF.
2. Auth Service begär inloggningsuppgifter.
3. Auth Service anropar User Service via **gRPC** för att hämta lagrat lösenord och userId.
4. Om lösenordet stämmer utfärdas en JWT-token som returneras till frontenden.

### Meddelandeflöde (Asynkront)
1. Användaren skickar ett meddelande via BFF till Message Service.
2. Message Service hämtar användarens profil via **gRPC**.
3. Meddelandet sparas i MessageDB.
4. En händelse (`message.published`) skickas till **RabbitMQ**.
5. Bot Service reagerar på händelsen, analyserar texten och skickar vid behov ett svar tillbaka via Message Services REST-API.

## Miljökonfiguration
Systemet använder miljövariabler med fallbacks för att kunna köras sömlöst i olika miljöer utan kodändringar. Se `application.yml` i respektive tjänst för detaljer om `${VARIABLE:default}` mönstret.