package com.ridesharing.services;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ridesharing.model.Driver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Microservicio para gestión de conductores
 * Maneja operaciones CRUD sobre conductores y disponibilidad
 */
public class DriverService implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final Map<String, Driver> driversDB = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String operation = (String) input.get("operation");
            context.getLogger().log("Operation: " + operation);

            switch (operation) {
                case "CREATE":
                    response = createDriver(input, context);
                    break;
                case "GET":
                    response = getDriver(input, context);
                    break;
                case "LIST":
                    response = listDrivers(context);
                    break;
                case "LIST_AVAILABLE":
                    response = listAvailableDrivers(context);
                    break;
                case "UPDATE":
                    response = updateDriver(input, context);
                    break;
                case "UPDATE_STATUS":
                    response = updateDriverStatus(input, context);
                    break;
                case "DELETE":
                    response = deleteDriver(input, context);
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

    private Map<String, Object> createDriver(Map<String, Object> input, Context context) {
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
            Driver driver = new Driver();
            driver.setId(id);
            driver.setName(jsonBody.get("name").getAsString());
            driver.setEmail(jsonBody.get("email").getAsString());
            driver.setPhone(jsonBody.get("phone").getAsString());
            driver.setVehicleModel(jsonBody.get("vehicleModel").getAsString());
            driver.setLicensePlate(jsonBody.get("licensePlate").getAsString());
            driver.setRating(5.0);
            driver.setAvailable(true); // Por defecto disponible

            driversDB.put(id, driver);

            context.getLogger().log("Driver created: " + id);

            response.put("statusCode", 201);
            response.put("body", gson.toJson(driver));
            
        } catch (Exception e) {
            context.getLogger().log("Error creating driver: " + e.getMessage());
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid driver data: " + e.getMessage() + "\"}");
        }

        return response;
    }

    private Map<String, Object> getDriver(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String driverId = (String) input.get("driverId");
            Driver driver = driversDB.get(driverId);
            
            if (driver != null) {
                response.put("statusCode", 200);
                response.put("body", gson.toJson(driver));
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"Driver not found\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }

    private Map<String, Object> listDrivers(Context context) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("body", gson.toJson(driversDB.values()));
        return response;
    }

    /**
     * Listar solo conductores disponibles
     */
    private Map<String, Object> listAvailableDrivers(Context context) {
        Map<String, Object> response = new HashMap<>();
        
        List<Driver> availableDrivers = driversDB.values().stream()
                .filter(Driver::isAvailable)
                .collect(Collectors.toList());
        
        response.put("statusCode", 200);
        response.put("body", gson.toJson(availableDrivers));
        return response;
    }

    private Map<String, Object> updateDriver(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String driverId = (String) input.get("driverId");
            Object bodyObj = input.get("body");
            JsonObject jsonBody = null;
            
            // Manejar body como String o Map
            if (bodyObj instanceof String) {
                jsonBody = gson.fromJson((String) bodyObj, JsonObject.class);
            } else if (bodyObj instanceof Map) {
                String bodyStr = gson.toJson((Map<String, Object>) bodyObj);
                jsonBody = gson.fromJson(bodyStr, JsonObject.class);
            }

            Driver driver = driversDB.get(driverId);
            
            if (driver != null) {
                if (jsonBody.has("name")) {
                    driver.setName(jsonBody.get("name").getAsString());
                }
                if (jsonBody.has("vehicleModel")) {
                    driver.setVehicleModel(jsonBody.get("vehicleModel").getAsString());
                }
                if (jsonBody.has("licensePlate")) {
                    driver.setLicensePlate(jsonBody.get("licensePlate").getAsString());
                }

                driversDB.put(driverId, driver);

                response.put("statusCode", 200);
                response.put("body", gson.toJson(driver));
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"Driver not found\"}");
            }

        } catch (Exception e) {
            context.getLogger().log("Error updating driver: " + e.getMessage());
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid data\"}");
        }

        return response;
    }
    /**
     * Actualizar solo el estado de disponibilidad del conductor
     */
    private Map<String, Object> updateDriverStatus(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String driverId = (String) input.get("driverId");
            Object bodyObj = input.get("body");
            JsonObject jsonBody = null;
            
            // Manejar body como String o Map
            if (bodyObj instanceof String) {
                jsonBody = gson.fromJson((String) bodyObj, JsonObject.class);
            } else if (bodyObj instanceof Map) {
                String bodyStr = gson.toJson((Map<String, Object>) bodyObj);
                jsonBody = gson.fromJson(bodyStr, JsonObject.class);
            }

            Driver driver = driversDB.get(driverId);
            
            if (driver != null) {
                boolean available = jsonBody.get("available").getAsBoolean();
                driver.setAvailable(available);
                driversDB.put(driverId, driver);

                context.getLogger().log("Driver " + driverId + " availability: " + available);

                response.put("statusCode", 200);
                response.put("body", gson.toJson(driver));
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"Driver not found\"}");
            }

        } catch (Exception e) {
            context.getLogger().log("Error updating driver status: " + e.getMessage());
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid data\"}");
        }

        return response;
    }

    
    private Map<String, Object> deleteDriver(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String driverId = (String) input.get("driverId");
            
            if (driversDB.containsKey(driverId)) {
                driversDB.remove(driverId);
                response.put("statusCode", 204);
                response.put("body", "");
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"Driver not found\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }
}