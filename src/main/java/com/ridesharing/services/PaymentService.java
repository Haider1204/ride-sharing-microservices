package com.ridesharing.services;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ridesharing.model.Payment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Microservicio para gestión de pagos
 * Procesa pagos de viajes completados
 */
public class PaymentService implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final Map<String, Payment> paymentsDB = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String operation = (String) input.get("operation");
            context.getLogger().log("Operation: " + operation);

            switch (operation) {
                case "CREATE":
                    response = processPayment(input, context);
                    break;
                case "GET":
                    response = getPayment(input, context);
                    break;
                case "LIST":
                    response = listPayments(context);
                    break;
                case "GET_BY_RIDE":
                    response = getPaymentByRide(input, context);
                    break;
                case "UPDATE":
                    response = updatePayment(input, context);
                    break;
                default:
                    response.put("statusCode", 400);
                    response.put("body", "{\"error\": \"Invalid operation\"}");
            }

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            response.put("statusCode", 500);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }

    /**
     * Procesar un nuevo pago
     */
    private Map<String, Object> processPayment(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Object bodyObj = input.get("body");
            JsonObject jsonBody = null;
            
            // Manejar body como String o Map
            if (bodyObj instanceof String) {
                jsonBody = gson.fromJson((String) bodyObj, JsonObject.class);
            } else if (bodyObj instanceof Map) {
                String bodyStr = gson.toJson((Map<String, Object>) bodyObj);
                jsonBody = gson.fromJson(bodyStr, JsonObject.class);
            } else {
                throw new Exception("Invalid body format");
            }

            String id = UUID.randomUUID().toString();
            Payment payment = new Payment();
            payment.setId(id);
            payment.setRideId(jsonBody.get("rideId").getAsString());
            payment.setAmount(jsonBody.get("amount").getAsDouble());
            payment.setMethod(jsonBody.get("method").getAsString());
            payment.setTimestamp(LocalDateTime.now().format(formatter));

            // Simular procesamiento de pago
            boolean paymentSuccess = processPaymentWithProvider(payment, context);
            
            if (paymentSuccess) {
                payment.setStatus("COMPLETED");
                context.getLogger().log("Payment processed successfully: " + id);
            } else {
                payment.setStatus("FAILED");
                context.getLogger().log("Payment failed: " + id);
            }

            paymentsDB.put(id, payment);

            response.put("statusCode", 201);
            response.put("body", gson.toJson(payment));
            
        } catch (Exception e) {
            context.getLogger().log("Error processing payment: " + e.getMessage());
            e.printStackTrace();
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid payment data: " + e.getMessage() + "\"}");
        }

        return response;
    }

    /**
     * Simula el procesamiento del pago con un proveedor externo
     * En producción, aquí se integraría con Stripe, PayPal, etc.
     */
    private boolean processPaymentWithProvider(Payment payment, Context context) {
        context.getLogger().log("Processing payment with provider...");
        context.getLogger().log("Method: " + payment.getMethod());
        context.getLogger().log("Amount: " + payment.getAmount());
        
        // Simulación: 95% de pagos exitosos
        Random random = new Random();
        return random.nextDouble() > 0.05;
    }

    private Map<String, Object> getPayment(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String paymentId = (String) input.get("paymentId");
            Payment payment = paymentsDB.get(paymentId);
            
            if (payment != null) {
                response.put("statusCode", 200);
                response.put("body", gson.toJson(payment));
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"Payment not found\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }

    private Map<String, Object> listPayments(Context context) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("body", gson.toJson(paymentsDB.values()));
        return response;
    }

    /**
     * Obtener el pago asociado a un viaje específico
     */
    private Map<String, Object> getPaymentByRide(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String rideId = (String) input.get("rideId");
            
            Optional<Payment> payment = paymentsDB.values().stream()
                    .filter(p -> p.getRideId().equals(rideId))
                    .findFirst();
            
            if (payment.isPresent()) {
                response.put("statusCode", 200);
                response.put("body", gson.toJson(payment.get()));
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"Payment not found for this ride\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }

    /**
     * Actualizar estado de un pago (ej: reintentar pago fallido)
     */
    private Map<String, Object> updatePayment(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String paymentId = (String) input.get("paymentId");
            Object bodyObj = input.get("body");
            JsonObject jsonBody = null;
            
            // Manejar body como String o Map
            if (bodyObj instanceof String) {
                jsonBody = gson.fromJson((String) bodyObj, JsonObject.class);
            } else if (bodyObj instanceof Map) {
                String bodyStr = gson.toJson((Map<String, Object>) bodyObj);
                jsonBody = gson.fromJson(bodyStr, JsonObject.class);
            }

            Payment payment = paymentsDB.get(paymentId);
            
            if (payment != null) {
                String newStatus = jsonBody.get("status").getAsString();
                payment.setStatus(newStatus);
                paymentsDB.put(paymentId, payment);

                context.getLogger().log("Payment " + paymentId + " status updated to: " + newStatus);

                response.put("statusCode", 200);
                response.put("body", gson.toJson(payment));
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"Payment not found\"}");
            }

        } catch (Exception e) {
            context.getLogger().log("Error updating payment: " + e.getMessage());
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid data\"}");
        }

        return response;
    }
}