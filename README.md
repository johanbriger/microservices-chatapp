
# Distribuerat Chattsystem (Mikrotjänster i Kubernetes)

Detta projekt är en händelsedriven chattapplikation byggd med Java Spring Boot, gRPC och RabbitMQ, designad för att köras i ett Kubernetes-kluster.

## Arkitekturöversikt
Systemet är uppdelat i flera oberoende mikrotjänster:
- **BFF-Service (Port 8080):** Backend-for-Frontend och API Gateway. Serverar även frontend-gränssnittet.
- **Auth-Service (Port 8083):** Hanterar inloggning och utfärdar JWT-tokens.
- **User-Service (Port 8081 / gRPC 9090):** Hanterar användardata (Source of Truth).
- **Message-Service (Port 8082):** Hanterar chattmeddelanden och publicerar händelser till RabbitMQ.
- **Bot-Service:** En asynkron konsument som lyssnar på meddelanden och svarar automatiskt.
- **RabbitMQ:** Message Broker för asynkron kommunikation.

## Förutsättningar
- **Docker Desktop** (med Kubernetes aktiverat) eller **Minikube**
- **Maven** (för att bygga JAR-filer)
- **Minikube Tunnel** (för att nå tjänsterna via Ingress)

## Installationsinstruktioner

### 1. Konfigurera lokala domännamn
Lägg till följande rad i din `hosts`-fil (`C:\Windows\System32\drivers\etc\hosts` på Windows eller `/etc/hosts` på Mac/Linux):
```text
127.0.0.1 chatapp.local

```

### 2. Starta klustret och Ingress

```bash
minikube start
minikube addons enable ingress

```

### 3. Bygg applikationen

Kör följande i projektets rotmapp:

```bash
mvn clean package -DskipTests

```

### 4. Bygg och ladda Docker-avbildningar

Bygg bilderna för varje tjänst och ladda in dem i Minikubes interna register:

```bash
# Exempel för BFF (upprepa för auth, user, message, bot)
docker build -t chatapp-bff:v4 ./bff-service
minikube image load chatapp-bff:v4

# Snabblista för övriga:
docker build -t chatapp-auth:v1 ./auth-service && minikube image load chatapp-auth:v1
docker build -t chatapp-user:v1 ./user-service && minikube image load chatapp-user:v1
docker build -t chatapp-message:v1 ./message-service && minikube image load chatapp-message:v1
docker build -t chatapp-bot:v4 ./bot-service && minikube image load chatapp-bot:v4

```

### 5. Driftsättning (Kubernetes)

Applicera alla konfigurationer och tjänster:

```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/rabbitmq.yaml
kubectl apply -f k8s/user-service.yaml
kubectl apply -f k8s/message-service.yaml
kubectl apply -f k8s/auth-service.yaml
kubectl apply -f k8s/bff-service.yaml
kubectl apply -f k8s/bot-service.yaml
kubectl apply -f k8s/ingress.yaml

```

### 6. Starta nätverkstunnel

För att Ingressen ska bli nåbar på din lokala maskin:

```bash
minikube tunnel

```

## Användning

1. Öppna webbläsaren och gå till `http://chatapp.local`.
2. Skapa ett konto (anropet går via BFF -> Auth -> User).
3. Logga in och börja chatta.
4. Skriv ett meddelande som innehåller ordet **"bot"** eller **"hej"** för att se Bot-Service svara asynkront via RabbitMQ.

## Felsökning

* **Kolla loggar:** `kubectl logs -l app=bot-service`
* **Se podd-status:** `kubectl get pods`
* **RabbitMQ Management UI:** Kör `kubectl port-forward deployment/rabbitmq 15672:15672` och gå till `http://localhost:15672` (guest/guest).

