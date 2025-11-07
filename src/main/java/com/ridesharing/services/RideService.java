package com.ridesharing.services;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ridesharing.model.Location;
import com.ridesharing.model.Ride;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Microservicio para gestión de viajes
 * Maneja el ciclo de vida completo de un viaje
 */
public class RideService implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final Map<String, Ride> ridesDB = new ConcurrentHashMap<>();
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
                    response = createRide(input, context);
                    break;
                case "GET":
                    response = getRide(input, context);
                    break;
                case "LIST":
                    response = listRides(context);
                    break;
                case "LIST_BY_USER":
                    response = listRidesByUser(input, context);
                    break;
                case "LIST_BY_DRIVER":
                    response = listRidesByDriver(input, context);
                    break;
                case "ACCEPT":
                    response = acceptRide(input, context);
                    break;
                case "START":
                    response = startRide(input, context);
                    break;
                case "COMPLETE":
                    response = completeRide(input, context);
                    break;
                case "CANCEL":
                    response = cancelRide(input, context);
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

    private Map<String, Object> createRide(Map<String, Object> input, Context context) {
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
            Ride ride = new Ride();
            ride.setId(id);
            ride.setUserId(jsonBody.get("userId").getAsString());
            ride.setStatus("REQUESTED");
            ride.setRequestTime(LocalDateTime.now().format(formatter));

            // Parse pickup location
            JsonObject pickup = jsonBody.getAsJsonObject("pickupLocation");
            Location pickupLoc = new Location(
                pickup.get("address").getAsString(),
                pickup.get("latitude").getAsDouble(),
                pickup.get("longitude").getAsDouble()
            );
            ride.setPickupLocation(pickupLoc);

            // Parse dropoff location
            JsonObject dropoff = jsonBody.getAsJsonObject("dropoffLocation");
            Location dropoffLoc = new Location(
                dropoff.get("address").getAsString(),
                dropoff.get("latitude").getAsDouble(),
                dropoff.get("longitude").getAsDouble()
            );
            ride.setDropoffLocation(dropoffLoc);

            // Calcular tarifa estimada (simplificado)
            double fare = calculateFare(pickupLoc, dropoffLoc);
            ride.setFare(fare);

            ridesDB.put(id, ride);

            context.getLogger().log("Ride created: " + id);

            response.put("statusCode", 201);
            response.put("body", gson.toJson(ride));
            
        } catch (Exception e) {
            context.getLogger().log("Error creating ride: " + e.getMessage());
            e.printStackTrace();
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid ride data: " + e.getMessage() + "\"}");
        }

        return response;
    }
    /**
     * Calcular tarifa basada en distancia estimada
     */
    private double calculateFare(Location pickup, Location dropoff) {
        // Fórmula simplificada de Haversine para calcular distancia
        double lat1 = Math.toRadians(pickup.getLatitude());
        double lon1 = Math.toRadians(pickup.getLongitude());
        double lat2 = Math.toRadians(dropoff.getLatitude());
        double lon2 = Math.toRadians(dropoff.getLongitude());

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double a = Math.pow(Math.sin(dlat / 2), 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        double radius = 6371; // Radio de la Tierra en km

        double distance = c * radius;

        // Tarifa: $2000 base + $1500 por km
        return 2000 + (distance * 1500);
    }

    private Map<String, Object> getRide(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String rideId = (String) input.get("rideId");
            Ride ride = ridesDB.get(rideId);
            
            if (ride != null) {
                response.put("statusCode", 200);
                response.put("body", gson.toJson(ride));
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"Ride not found\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }

    private Map<String, Object> listRides(Context context) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("body", gson.toJson(ridesDB.values()));
        return response;
    }

    private Map<String, Object> listRidesByUser(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        String userId = (String) input.get("userId");
        
        List<Ride> userRides = ridesDB.values().stream()
                .filter(ride -> ride.getUserId().equals(userId))
                .collect(Collectors.toList());
        
        response.put("statusCode", 200);
        response.put("body", gson.toJson(userRides));
        return response;
    }

    private Map<String, Object> listRidesByDriver(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        String driverId = (String) input.get("driverId");
        
        List<Ride> driverRides = ridesDB.values().stream()
                .filter(ride -> driverId.equals(ride.getDriverId()))
                .collect(Collectors.toList());
        
        response.put("statusCode", 200);
        response.put("body", gson.toJson(driverRides));
        return response;
    }

    private Map<String, Object> acceptRide(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String rideId = (String) input.get("rideId");
            Object bodyObj = input.get("body");
            JsonObject jsonBody = null;
            
            // Manejar body como String o Map
            if (bodyObj instanceof String) {
                jsonBody = gson.fromJson((String) bodyObj, JsonObject.class);
            } else if (bodyObj instanceof Map) {
                String bodyStr = gson.toJson((Map<String, Object>) bodyObj);
                jsonBody = gson.fromJson(bodyStr, JsonObject.class);
            }

            Ride ride = ridesDB.get(rideId);
            
            if (ride != null && "REQUESTED".equals(ride.getStatus())) {
                String driverId = jsonBody.get("driverId").getAsString();
                ride.setDriverId(driverId);
                ride.setStatus("ACCEPTED");
                ridesDB.put(rideId, ride);

                context.getLogger().log("Ride " + rideId + " accepted by driver " + driverId);

                response.put("statusCode", 200);
                response.put("body", gson.toJson(ride));
            } else {
                response.put("statusCode", 400);
                response.put("body", "{\"error\": \"Ride cannot be accepted\"}");
            }

        } catch (Exception e) {
            context.getLogger().log("Error accepting ride: " + e.getMessage());
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid data\"}");
        }

        return response;
    }

    private Map<String, Object> startRide(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String rideId = (String) input.get("rideId");
            Ride ride = ridesDB.get(rideId);
            
            if (ride != null && "ACCEPTED".equals(ride.getStatus())) {
                ride.setStatus("IN_PROGRESS");
                ride.setStartTime(LocalDateTime.now().format(formatter));
                ridesDB.put(rideId, ride);

                context.getLogger().log("Ride " + rideId + " started");

                response.put("statusCode", 200);
                response.put("body", gson.toJson(ride));
            } else {
                response.put("statusCode", 400);
                response.put("body", "{\"error\": \"Ride cannot be started\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid data\"}");
        }

        return response;
    }

    private Map<String, Object> completeRide(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String rideId = (String) input.get("rideId");
            Ride ride = ridesDB.get(rideId);
            
            if (ride != null && "IN_PROGRESS".equals(ride.getStatus())) {
                ride.setStatus("COMPLETED");
                ride.setEndTime(LocalDateTime.now().format(formatter));
                ridesDB.put(rideId, ride);

                context.getLogger().log("Ride " + rideId + " completed");

                response.put("statusCode", 200);
                response.put("body", gson.toJson(ride));
            } else {
                response.put("statusCode", 400);
                response.put("body", "{\"error\": \"Ride cannot be completed\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid data\"}");
        }

        return response;
    }

    private Map<String, Object> cancelRide(Map<String, Object> input, Context context) {
    Map<String, Object> response = new HashMap<>();
    
        try {
            String rideId = (String) input.get("rideId");
            Ride ride = ridesDB.get(rideId);
            
            if (ride != null && !"COMPLETED".equals(ride.getStatus())) {
                ride.setStatus("CANCELLED");
                ridesDB.put(rideId, ride);

                context.getLogger().log("Ride " + rideId + " cancelled");

                response.put("statusCode", 200);
                response.put("body", gson.toJson(ride));
            } else {
                response.put("statusCode", 400);
                response.put("body", "{\"error\": \"Ride cannot be cancelled\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid data\"}");
        }

        return response;
    }
}