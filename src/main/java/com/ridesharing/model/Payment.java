package com.ridesharing.model;

public class Payment {
    private String id;
    private String rideId;
    private double amount;
    private String method; // CREDIT_CARD, DEBIT_CARD, CASH, WALLET
    private String status; // PENDING, COMPLETED, FAILED
    private String timestamp;

    // Constructor vacío
    public Payment() {
    }

    // Constructor con parámetros
    public Payment(String id, String rideId, double amount, 
                   String method, String status, String timestamp) {
        this.id = id;
        this.rideId = rideId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", rideId='" + rideId + '\'' +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}