package com.ridesharing.services;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ridesharing.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Microservicio para gestión de usuarios
 * Maneja operaciones CRUD sobre usuarios
 */
public class UserService implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    // Simulación de base de datos en memoria
    private static final Map<String, User> usersDB = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Obtener el método HTTP y la operación
            String httpMethod = (String) input.get("httpMethod");
            String operation = (String) input.get("operation");
            String pathParameters = (String) input.get("pathParameters");

            context.getLogger().log("HTTP Method: " + httpMethod);
            context.getLogger().log("Operation: " + operation);

            switch (operation) {
                case "CREATE":
                    response = createUser(input, context);
                    break;
                case "GET":
                    response = getUser(input, context);
                    break;
                case "LIST":
                    response = listUsers(context);
                    break;
                case "UPDATE":
                    response = updateUser(input, context);
                    break;
                case "DELETE":
                    response = deleteUser(input, context);
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
     * CREATE - Crear un nuevo usuario
     */
    private Map<String, Object> createUser(Map<String, Object> input, Context context) {
    Map<String, Object> response = new HashMap<>();
    
        try {
            context.getLogger().log("Input received: " + input.toString());
            
            // El body puede venir como String o como Map (ya parseado por API Gateway)
            Object bodyObj = input.get("body");
            JsonObject jsonBody = null;
            
            if (bodyObj instanceof String) {
                // Si es String, parsear JSON
                String bodyStr = (String) bodyObj;
                context.getLogger().log("Body as String: " + bodyStr);
                jsonBody = gson.fromJson(bodyStr, JsonObject.class);
                
            } else if (bodyObj instanceof Map) {
                // Si ya es Map, convertir a JSON y parsear
                Map<String, Object> bodyMap = (Map<String, Object>) bodyObj;
                context.getLogger().log("Body as Map: " + bodyMap.toString());
                String bodyStr = gson.toJson(bodyMap);
                jsonBody = gson.fromJson(bodyStr, JsonObject.class);
                
            } else {
                context.getLogger().log("Body type: " + (bodyObj != null ? bodyObj.getClass().getName() : "null"));
                throw new Exception("Invalid body format");
            }
            
            context.getLogger().log("Parsed JSON: " + jsonBody.toString());
            
            // Validar campos requeridos
            if (!jsonBody.has("name") || !jsonBody.has("email") || !jsonBody.has("phone")) {
                throw new Exception("Missing required fields: name, email, or phone");
            }
            
            // Crear nuevo usuario
            String id = UUID.randomUUID().toString();
            User user = new User();
            user.setId(id);
            user.setName(jsonBody.get("name").getAsString());
            user.setEmail(jsonBody.get("email").getAsString());
            user.setPhone(jsonBody.get("phone").getAsString());
            user.setRating(5.0); // Rating inicial

            // Guardar en "base de datos"
            usersDB.put(id, user);

            context.getLogger().log("User created successfully: " + id);

            response.put("statusCode", 201);
            response.put("body", gson.toJson(user));
            
        } catch (Exception e) {
            context.getLogger().log("Error creating user: " + e.getMessage());
            e.printStackTrace();
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }
    /**
     * GET - Obtener un usuario por ID
     */
    private Map<String, Object> getUser(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userId = (String) input.get("userId");
            
            if (userId == null || userId.isEmpty()) {
                response.put("statusCode", 400);
                response.put("body", "{\"error\": \"userId is required\"}");
                return response;
            }

            User user = usersDB.get(userId);
            
            if (user != null) {
                response.put("statusCode", 200);
                response.put("body", gson.toJson(user));
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"User not found\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }

    /**
     * LIST - Listar todos los usuarios
     */
    private Map<String, Object> listUsers(Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("statusCode", 200);
            response.put("body", gson.toJson(usersDB.values()));
            
        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }

    /**
    * UPDATE - Actualizar un usuario
    */
    private Map<String, Object> updateUser(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userId = (String) input.get("userId");
            Object bodyObj = input.get("body");
            JsonObject jsonBody = null;
            
            // Manejar body como String o Map
            if (bodyObj instanceof String) {
                jsonBody = gson.fromJson((String) bodyObj, JsonObject.class);
            } else if (bodyObj instanceof Map) {
                String bodyStr = gson.toJson((Map<String, Object>) bodyObj);
                jsonBody = gson.fromJson(bodyStr, JsonObject.class);
            }

            User user = usersDB.get(userId);
            
            if (user != null) {
                // Actualizar campos
                if (jsonBody.has("name")) {
                    user.setName(jsonBody.get("name").getAsString());
                }
                if (jsonBody.has("email")) {
                    user.setEmail(jsonBody.get("email").getAsString());
                }
                if (jsonBody.has("phone")) {
                    user.setPhone(jsonBody.get("phone").getAsString());
                }

                usersDB.put(userId, user);

                response.put("statusCode", 200);
                response.put("body", gson.toJson(user));
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"User not found\"}");
            }

        } catch (Exception e) {
            context.getLogger().log("Error updating user: " + e.getMessage());
            response.put("statusCode", 400);
            response.put("body", "{\"error\": \"Invalid data\"}");
        }

        return response;
    }

    /**
     * DELETE - Eliminar un usuario
     */
    private Map<String, Object> deleteUser(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userId = (String) input.get("userId");
            
            if (usersDB.containsKey(userId)) {
                usersDB.remove(userId);
                response.put("statusCode", 204);
                response.put("body", "");
            } else {
                response.put("statusCode", 404);
                response.put("body", "{\"error\": \"User not found\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }
}