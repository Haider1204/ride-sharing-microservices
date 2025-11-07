# Ride-Sharing Microservices Platform

## 📋 Descripción del Proyecto

Este proyecto implementa una arquitectura de microservicios para una plataforma de ride-sharing (tipo Uber) utilizando **AWS Lambda** y **API Gateway**. El sistema permite gestionar usuarios, conductores, viajes y pagos de manera independiente y escalable.

## 🎯 Metáfora de Diseño

### "Organización de pequeñas empresas independientes"

Cada microservicio funciona como una **pequeña empresa autónoma** con una responsabilidad única y bien definida:

- **UserService** → "Departamento de Recursos Humanos" - Gestiona información de pasajeros
- **DriverService** → "Departamento de Conductores" - Administra la flota de conductores
- **RideService** → "Centro de Operaciones" - Coordina los viajes y su ciclo de vida
- **PaymentService** → "Departamento Financiero" - Procesa todos los pagos

Estas "empresas" se comunican entre sí a través de **APIs REST bien definidas**, manteniendo su independencia y permitiendo que cada una escale, evolucione y se despliegue de forma autónoma.

## 🏗️ Arquitectura del Sistema
```
┌─────────────┐
│   Cliente   │
│  (Web/App)  │
└──────┬──────┘
       │
       │ HTTPS
       ▼
┌─────────────────────────┐
│    API Gateway          │
│  (Punto de Entrada)     │
└────┬────┬────┬────┬─────┘
     │    │    │    │
     │    │    │    │
     ▼    ▼    ▼    ▼
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│ User   │ │ Driver │ │ Ride   │ │Payment │
│Service │ │Service │ │Service │ │Service │
│(Lambda)│ │(Lambda)│ │(Lambda)│ │(Lambda)│
└────────┘ └────────┘ └────────┘ └────────┘
```

### Características de la Arquitectura

1. **Desacoplamiento** - Cada servicio es independiente
2. **Escalabilidad** - AWS Lambda escala automáticamente según demanda
3. **Tolerancia a fallos** - Si un servicio falla, los demás continúan operando
4. **Serverless** - No hay servidores que gestionar
5. **Pay-per-use** - Solo pagas por ejecuciones reales

## 🔧 Tecnologías Utilizadas

- **Lenguaje:** Java 11
- **Build Tool:** Maven
- **Cloud Provider:** AWS
  - AWS Lambda (Compute)
  - API Gateway (Routing)
  - IAM (Security)
- **Librerías:**
  - AWS Lambda Core
  - AWS Lambda Events
  - Gson (JSON parsing)

## 📦 Recursos y Modelos de Datos

### 1. User (Usuario)
```json
{
  "id": "uuid",
  "name": "string",
  "email": "string",
  "phone": "string",
  "rating": "double"
}
```

### 2. Driver (Conductor)
```json
{
  "id": "uuid",
  "name": "string",
  "email": "string",
  "phone": "string",
  "rating": "double",
  "vehicleModel": "string",
  "licensePlate": "string",
  "available": "boolean"
}
```

### 3. Ride (Viaje)
```json
{
  "id": "uuid",
  "userId": "string",
  "driverId": "string",
  "pickupLocation": {
    "address": "string",
    "latitude": "double",
    "longitude": "double"
  },
  "dropoffLocation": {
    "address": "string",
    "latitude": "double",
    "longitude": "double"
  },
  "status": "REQUESTED | ACCEPTED | IN_PROGRESS | COMPLETED | CANCELLED",
  "fare": "double",
  "requestTime": "ISO-8601",
  "startTime": "ISO-8601",
  "endTime": "ISO-8601"
}
```

### 4. Payment (Pago)
```json
{
  "id": "uuid",
  "rideId": "string",
  "amount": "double",
  "method": "CREDIT_CARD | DEBIT_CARD | CASH | WALLET",
  "status": "PENDING | COMPLETED | FAILED",
  "timestamp": "ISO-8601"
}
```

## 🌐 API Endpoints

### Base URL
```
https://YOUR-API-ID.execute-api.REGION.amazonaws.com/prod
```

### Users API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/users` | Listar todos los usuarios |
| GET | `/users/{id}` | Obtener usuario por ID |
| POST | `/users` | Crear nuevo usuario |
| PUT | `/users/{id}` | Actualizar usuario |
| DELETE | `/users/{id}` | Eliminar usuario |

### Drivers API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/drivers` | Listar todos los conductores |
| GET | `/drivers/available` | Listar conductores disponibles |
| GET | `/drivers/{id}` | Obtener conductor por ID |
| POST | `/drivers` | Crear nuevo conductor |
| PUT | `/drivers/{id}` | Actualizar conductor |
| PUT | `/drivers/{id}/status` | Cambiar disponibilidad |
| DELETE | `/drivers/{id}` | Eliminar conductor |

### Rides API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/rides` | Listar todos los viajes |
| GET | `/rides/{id}` | Obtener viaje por ID |
| GET | `/rides/user/{userId}` | Obtener viajes de un usuario |
| GET | `/rides/driver/{driverId}` | Obtener viajes de un conductor |
| POST | `/rides` | Crear solicitud de viaje |
| PUT | `/rides/{id}/accept` | Conductor acepta viaje |
| PUT | `/rides/{id}/start` | Iniciar viaje |
| PUT | `/rides/{id}/complete` | Completar viaje |
| PUT | `/rides/{id}/cancel` | Cancelar viaje |

### Payments API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/payments` | Listar todos los pagos |
| GET | `/payments/{id}` | Obtener pago por ID |
| GET | `/payments/ride/{rideId}` | Obtener pago de un viaje |
| POST | `/payments` | Procesar nuevo pago |
| PUT | `/payments/{id}` | Actualizar estado del pago |

## 🚀 Guía de Uso - Ejemplos

### 1. Crear un usuario
```bash
curl -X POST https://YOUR-API-URL/prod/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "phone": "+573001234567"
  }'
```

**Respuesta:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+573001234567",
  "rating": 5.0
}
```

### 2. Crear un conductor
```bash
curl -X POST https://YOUR-API-URL/prod/drivers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Carlos López",
    "email": "carlos@example.com",
    "phone": "+573009876543",
    "vehicleModel": "Toyota Corolla 2020",
    "licensePlate": "ABC123"
  }'
```

### 3. Solicitar un viaje
```bash
curl -X POST https://YOUR-API-URL/prod/rides \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "pickupLocation": {
      "address": "Calle 100 #15-20, Bogotá",
      "latitude": 4.6876,
      "longitude": -74.0548
    },
    "dropoffLocation": {
      "address": "Calle 50 #20-30, Bogotá",
      "latitude": 4.6543,
      "longitude": -74.0678
    }
  }'
```

**Respuesta:**
```json
{
  "id": "ride-uuid",
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "status": "REQUESTED",
  "pickupLocation": {...},
  "dropoffLocation": {...},
  "fare": 8500.50,
  "requestTime": "2024-11-06T14:30:00"
}
```

### 4. Conductor acepta el viaje
```bash
curl -X PUT https://YOUR-API-URL/prod/rides/ride-uuid/accept \
  -H "Content-Type: application/json" \
  -d '{
    "driverId": "driver-uuid"
  }'
```

### 5. Flujo completo de un viaje
```bash
# 1. Iniciar viaje
curl -X PUT https://YOUR-API-URL/prod/rides/ride-uuid/start

# 2. Completar viaje
curl -X PUT https://YOUR-API-URL/prod/rides/ride-uuid/complete

# 3. Procesar pago
curl -X POST https://YOUR-API-URL/prod/payments \
  -H "Content-Type: application/json" \
  -d '{
    "rideId": "ride-uuid",
    "amount": 8500.50,
    "method": "CREDIT_CARD"
  }'
```

## 🔐 Decisiones Arquitectónicas

### 1. ¿Por qué Microservicios?

**Ventajas:**
- ✅ **Desarrollo independiente** - Equipos pueden trabajar en paralelo
- ✅ **Despliegue independiente** - Actualizar un servicio no afecta a otros
- ✅ **Escalabilidad granular** - Escalar solo los servicios que lo necesiten
- ✅ **Tecnología heterogénea** - Cada servicio puede usar diferentes tecnologías
- ✅ **Tolerancia a fallos** - Un servicio caído no tumba todo el sistema

**Desventajas consideradas:**
- ⚠️ Mayor complejidad operacional
- ⚠️ Latencia de red entre servicios
- ⚠️ Consistencia eventual en lugar de transacciones ACID

### 2. ¿Por qué AWS Lambda?

- **Serverless** - No hay infraestructura que gestionar
- **Auto-escalado** - Maneja desde 1 hasta miles de solicitudes simultáneas
- **Pay-per-use** - Solo pagas por milisegundos de ejecución
- **Alta disponibilidad** - AWS gestiona la redundancia
- **Integración nativa** - Se integra perfectamente con API Gateway

### 3. ¿Por qué API Gateway?

- **Punto de entrada único** - Simplifica el acceso para clientes
- **Routing inteligente** - Dirige solicitudes al Lambda correcto
- **Seguridad** - Autenticación, autorización, rate limiting
- **Transformación** - Mapea requests/responses entre cliente y Lambda
- **Monitoreo** - Logs automáticos con CloudWatch

### 4. Almacenamiento en Memoria (Trade-off)

**Decisión:** En este prototipo usamos `ConcurrentHashMap` (memoria)

**Justificación:**
- ✅ Simplicidad para demostración
- ✅ Baja latencia
- ✅ Sin costos adicionales

**En producción se usaría:**
- DynamoDB (base de datos NoSQL serverless de AWS)
- RDS (base de datos relacional gestionada)
- ElastiCache (cache distribuido)

### 5. Comunicación Síncrona REST

**Decisión:** Los servicios exponen APIs REST

**Alternativas consideradas:**
- **Event-driven** (RabbitMQ, Kafka) - Para comunicación asíncrona
- **gRPC** - Para comunicación de alto rendimiento
- **GraphQL** - Para flexibilidad en consultas del cliente

**Justificación de REST:**
- ✅ Simplicidad y familiaridad
- ✅ Compatible con cualquier cliente HTTP
- ✅ Fácil debugging con herramientas estándar
- ✅ Semántica clara con métodos HTTP

## 📊 Patrones de Diseño Implementados

### 1. API-First Design
Diseñamos la API antes de implementar el código, asegurando contratos claros.

### 2. Single Responsibility Principle
Cada microservicio tiene UNA responsabilidad bien definida.

### 3. Stateless Services
Los servicios Lambda no mantienen estado entre invocaciones.

### 4. RESTful Design
Seguimos los principios REST:
- Recursos identificables por URIs
- Métodos HTTP estándar (GET, POST, PUT, DELETE)
- Respuestas en JSON
- Códigos de estado HTTP semánticos

### 5. Error Handling
Respuestas de error consistentes:
```json
{
  "error": "Descripción del error",
  "statusCode": 400
}
```

## 🛠️ Cómo Ejecutar el Proyecto

### Prerrequisitos

- Java 11+
- Maven 3.6+
- Cuenta de AWS
- AWS CLI configurado (opcional)

### Compilar el proyecto
```bash
cd ride-sharing-microservices
mvn clean package
```

El JAR se generará en: `target/ride-sharing-microservices-1.0-SNAPSHOT.jar`

### Desplegar en AWS

1. **Crear rol de ejecución en IAM**
   - Rol: `RideSharingLambdaRole`
   - Policy: `AWSLambdaBasicExecutionRole`

2. **Crear funciones Lambda**
   - UserService
   - DriverService
   - RideService
   - PaymentService

3. **Subir el JAR a cada función**

4. **Configurar handlers:**
   - `com.ridesharing.services.UserService::handleRequest`
   - `com.ridesharing.services.DriverService::handleRequest`
   - `com.ridesharing.services.RideService::handleRequest`
   - `com.ridesharing.services.PaymentService::handleRequest`

5. **Crear API Gateway**
   - Crear recursos y métodos según la tabla de endpoints
   - Configurar mapping templates
   - Desplegar en stage `prod`

## 📈 Mejoras Futuras

### Funcionalidades

- [ ] Autenticación y autorización (AWS Cognito)
- [ ] Tracking en tiempo real (WebSockets con API Gateway)
- [ ] Notificaciones push (AWS SNS)
- [ ] Sistema de ratings bidireccional
- [ ] Gestión de promociones y cupones
- [ ] Histórico de viajes con búsqueda avanzada

### Arquitectura

- [ ] Persistencia con DynamoDB
- [ ] Cache con ElastiCache/Redis
- [ ] Comunicación asíncrona con SQS/EventBridge
- [ ] Circuit Breaker pattern (Resilience4j)
- [ ] Observabilidad con X-Ray y CloudWatch
- [ ] CI/CD con AWS CodePipeline
- [ ] Infraestructura como código (Terraform/CloudFormation)

### Seguridad

- [ ] API Keys y rate limiting
- [ ] Encriptación de datos sensibles
- [ ] Validación exhaustiva de inputs
- [ ] WAF (Web Application Firewall)
- [ ] Secrets Manager para credenciales

## 📚 Referencias

- [AWS Lambda Documentation](https://docs.aws.amazon.com/lambda/)
- [API Gateway Best Practices](https://docs.aws.amazon.com/apigateway/latest/developerguide/best-practices.html)
- [RESTful API Design](https://restfulapi.net/)
- [Microservices Patterns](https://microservices.io/patterns/)
- [Building Microservices - Sam Newman](https://samnewman.io/books/building_microservices/)