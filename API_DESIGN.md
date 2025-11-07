# API Design - Ride Sharing Platform

## Arquitectura de Microservicios

Esta API sigue los principios REST y está diseñada con una arquitectura de microservicios donde cada servicio es independiente y tiene una responsabilidad única.

---

## Recursos Principales

1. **Users** - Gestión de usuarios (pasajeros)
2. **Drivers** - Gestión de conductores
3. **Rides** - Gestión de viajes
4. **Payments** - Gestión de pagos

---

## Endpoints Detallados

### 1. USERS API

**Base URI:** `/users`

#### 1.1 Listar todos los usuarios
```
GET /users
Response: 200 OK
[
  {
    "id": "user123",
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "phone": "+57300123456",
    "rating": 4.8
  }
]
```

#### 1.2 Obtener usuario por ID
```
GET /users/{id}
Response: 200 OK
{
  "id": "user123",
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+57300123456",
  "rating": 4.8
}
```

#### 1.3 Crear nuevo usuario
```
POST /users
Request Body:
{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+57300123456"
}
Response: 201 Created
{
  "id": "user123",
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+57300123456",
  "rating": 5.0
}
```

#### 1.4 Actualizar usuario
```
PUT /users/{id}
Request Body:
{
  "name": "Juan Pérez Actualizado",
  "email": "juan.nuevo@example.com",
  "phone": "+57300999888"
}
Response: 200 OK
```

#### 1.5 Eliminar usuario
```
DELETE /users/{id}
Response: 204 No Content
```

---

### 2. DRIVERS API

**Base URI:** `/drivers`

#### 2.1 Listar todos los conductores
```
GET /drivers
Response: 200 OK
```

#### 2.2 Obtener conductores disponibles
```
GET /drivers/available
Response: 200 OK
[
  {
    "id": "driver456",
    "name": "Carlos López",
    "rating": 4.9,
    "vehicleModel": "Toyota Corolla 2020",
    "licensePlate": "ABC123",
    "available": true
  }
]
```

#### 2.3 Obtener conductor por ID
```
GET /drivers/{id}
Response: 200 OK
```

#### 2.4 Crear nuevo conductor
```
POST /drivers
Request Body:
{
  "name": "Carlos López",
  "email": "carlos@example.com",
  "phone": "+57301234567",
  "vehicleModel": "Toyota Corolla 2020",
  "licensePlate": "ABC123"
}
Response: 201 Created
```

#### 2.5 Actualizar disponibilidad
```
PUT /drivers/{id}/status
Request Body:
{
  "available": false
}
Response: 200 OK
```

#### 2.6 Actualizar conductor
```
PUT /drivers/{id}
Response: 200 OK
```

#### 2.7 Eliminar conductor
```
DELETE /drivers/{id}
Response: 204 No Content
```

---

### 3. RIDES API

**Base URI:** `/rides`

#### 3.1 Listar todos los viajes
```
GET /rides
Response: 200 OK
```

#### 3.2 Obtener viaje por ID
```
GET /rides/{id}
Response: 200 OK
{
  "id": "ride789",
  "userId": "user123",
  "driverId": "driver456",
  "pickupLocation": {
    "address": "Calle 100 #15-20",
    "latitude": 4.6876,
    "longitude": -74.0548
  },
  "dropoffLocation": {
    "address": "Calle 50 #20-30",
    "latitude": 4.6543,
    "longitude": -74.0678
  },
  "status": "IN_PROGRESS",
  "fare": 15000,
  "requestTime": "2024-11-06T10:00:00Z"
}
```

#### 3.3 Obtener viajes por usuario
```
GET /rides/user/{userId}
Response: 200 OK
```

#### 3.4 Obtener viajes por conductor
```
GET /rides/driver/{driverId}
Response: 200 OK
```

#### 3.5 Crear solicitud de viaje
```
POST /rides
Request Body:
{
  "userId": "user123",
  "pickupLocation": {
    "address": "Calle 100 #15-20",
    "latitude": 4.6876,
    "longitude": -74.0548
  },
  "dropoffLocation": {
    "address": "Calle 50 #20-30",
    "latitude": 4.6543,
    "longitude": -74.0678
  }
}
Response: 201 Created
{
  "id": "ride789",
  "userId": "user123",
  "status": "REQUESTED",
  "pickupLocation": {...},
  "dropoffLocation": {...},
  "fare": 15000,
  "requestTime": "2024-11-06T10:00:00Z"
}
```

#### 3.6 Conductor acepta viaje
```
PUT /rides/{id}/accept
Request Body:
{
  "driverId": "driver456"
}
Response: 200 OK
{
  "id": "ride789",
  "status": "ACCEPTED",
  "driverId": "driver456"
}
```

#### 3.7 Iniciar viaje
```
PUT /rides/{id}/start
Response: 200 OK
{
  "id": "ride789",
  "status": "IN_PROGRESS",
  "startTime": "2024-11-06T10:15:00Z"
}
```

#### 3.8 Completar viaje
```
PUT /rides/{id}/complete
Response: 200 OK
{
  "id": "ride789",
  "status": "COMPLETED",
  "endTime": "2024-11-06T10:45:00Z"
}
```

#### 3.9 Cancelar viaje
```
PUT /rides/{id}/cancel
Request Body:
{
  "reason": "Usuario canceló"
}
Response: 200 OK
{
  "id": "ride789",
  "status": "CANCELLED"
}
```

---

### 4. PAYMENTS API

**Base URI:** `/payments`

#### 4.1 Listar todos los pagos
```
GET /payments
Response: 200 OK
```

#### 4.2 Obtener pago por ID
```
GET /payments/{id}
Response: 200 OK
```

#### 4.3 Obtener pago por viaje
```
GET /payments/ride/{rideId}
Response: 200 OK
{
  "id": "payment001",
  "rideId": "ride789",
  "amount": 15000,
  "method": "CREDIT_CARD",
  "status": "COMPLETED",
  "timestamp": "2024-11-06T10:50:00Z"
}
```

#### 4.4 Procesar pago
```
POST /payments
Request Body:
{
  "rideId": "ride789",
  "amount": 15000,
  "method": "CREDIT_CARD"
}
Response: 201 Created
{
  "id": "payment001",
  "rideId": "ride789",
  "amount": 15000,
  "method": "CREDIT_CARD",
  "status": "COMPLETED",
  "timestamp": "2024-11-06T10:50:00Z"
}
```

#### 4.5 Actualizar estado del pago
```
PUT /payments/{id}
Request Body:
{
  "status": "FAILED"
}
Response: 200 OK
```

---

## Códigos de Estado HTTP

- **200 OK** - Operación exitosa
- **201 Created** - Recurso creado exitosamente
- **204 No Content** - Operación exitosa sin contenido en respuesta
- **400 Bad Request** - Datos inválidos en la solicitud
- **404 Not Found** - Recurso no encontrado
- **500 Internal Server Error** - Error del servidor

---

## Formato de Respuestas

Todas las respuestas se devuelven en formato **JSON**.

### Respuesta de éxito:
```json
{
  "id": "...",
  "campo1": "valor1",
  "campo2": "valor2"
}
```

### Respuesta de error:
```json
{
  "error": "Descripción del error",
  "statusCode": 400,
  "timestamp": "2024-11-06T10:00:00Z"
}
```

