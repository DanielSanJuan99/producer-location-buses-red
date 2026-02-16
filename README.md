# Producer Location - Buses RED 📍

Microservicio productor que recibe coordenadas GPS de buses y las publica en RabbitMQ para su procesamiento asíncrono.

## Tecnologías

- Java 21 (Eclipse Temurin)
- Spring Boot 3.5.7
- Spring AMQP (RabbitMQ)
- Jackson (serialización JSON)
- Maven 3.9.9
- Docker (multi-stage build)

## Arquitectura

```
POST /location/send
        │
        ▼
┌─────────────────────┐
│  LocationController  │
│  (valida y responde) │
└────────┬────────────┘
         │
         ▼
┌─────────────────────────┐
│  LocationProducerService │
│  (publica en RabbitMQ)   │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│  RabbitMQ                │
│  Exchange: location.exchange │
│  Queue:    location.queue    │
│  Routing:  location.routing.key │
└─────────────────────────┘
```

## Estructura del Proyecto

```
producer-location-buses-red/
├── src/main/java/com/busesred/producer/location/
│   ├── ProducerLocationApplication.java
│   ├── config/
│   │   └── RabbitMQConfig.java
│   ├── controller/
│   │   └── LocationController.java
│   ├── model/
│   │   └── LocationMessage.java
│   └── service/
│       └── LocationProducerService.java
├── src/main/resources/
│   └── application.yml
├── Dockerfile
└── pom.xml
```

## Modelo de Datos (JSON)

Todos los campos usan español snake_case via `@JsonProperty`:

```json
{
  "id_bus": "BUS-001",
  "latitud": -33.4489,
  "longitud": -70.6693,
  "ruta": "506",
  "velocidad": 45.5,
  "marca_tiempo": "2026-02-15T10:30:00"
}
```

| Campo JSON | Campo Java | Tipo |
|---|---|---|
| `id_bus` | busId | String |
| `latitud` | latitude | Double |
| `longitud` | longitude | Double |
| `marca_tiempo` | timestamp | LocalDateTime |
| `ruta` | route | String |
| `velocidad` | speed | Double |

> `marca_tiempo` se genera automáticamente si no se envía en el payload.

## Variables de Entorno

| Variable | Descripción | Default |
|---|---|---|
| `RABBITMQ_HOST` | Host de RabbitMQ | `rabbitmq` |
| `RABBITMQ_PORT` | Puerto de RabbitMQ | `5672` |
| `RABBITMQ_USERNAME` | Usuario RabbitMQ | *(requerido)* |
| `RABBITMQ_PASSWORD` | Contraseña RabbitMQ | *(requerido)* |

## Configuración RabbitMQ

| Recurso | Nombre |
|---|---|
| Exchange | `location.exchange` (TopicExchange) |
| Queue | `location.queue` (durable) |
| Routing Key | `location.routing.key` |
| Converter | `Jackson2JsonMessageConverter` |

## Endpoints

### Enviar Ubicación
```http
POST /location/send
Content-Type: application/json

{
  "id_bus": "BUS-003",
  "ruta": "507",
  "latitud": -33.4489,
  "longitud": -70.6693,
  "velocidad": 45.5
}
```

**Respuesta (200 OK):**
```json
{
  "estado": "exitoso",
  "mensaje": "Ubicación enviada a RabbitMQ correctamente",
  "datos": { ... },
  "marca_tiempo": "2026-02-15T10:30:00"
}
```

### Health Check
```http
GET /location/health
```

## Ejecución Local

```bash
mvn clean package -DskipTests

RABBITMQ_USERNAME=admin RABBITMQ_PASSWORD=admin123 \
java -jar target/producer-location-buses-red-1.0.0.jar
```

## Docker

```bash
docker build --no-cache --platform linux/amd64 -t producer-location-buses-red:latest .

docker run -p 8081:8081 \
  -e RABBITMQ_HOST=rabbitmq \
  -e RABBITMQ_USERNAME=admin \
  -e RABBITMQ_PASSWORD=admin123 \
  producer-location-buses-red:latest
```

## Puerto

| Servicio | Puerto |
|---|---|
| Producer Location | `8081` |
