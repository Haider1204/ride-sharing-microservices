package com.ridesharing.model;

public class Ride {
    private String id;
    private String userId;
    private String driverId;
    private Location pickupLocation;
    private Location dropoffLocation;
    private String status; // REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
    private double fare;
    private String requestTime;
    private String startTime;
    private String endTime;

    // Constructor vacío
    public Ride() {
    }

    // Constructor con parámetros
    public Ride(String id, String userId, String driverId, 
                Location pickupLocation, Location dropoffLocation, 
                String status, double fare, String requestTime, 
                String startTime, String endTime) {
        this.id = id;
        this.userId = userId;
        this.driverId = driverId;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.status = status;
        this.fare = fare;
        this.requestTime = requestTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(Location dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", pickupLocation=" + pickupLocation +
                ", dropoffLocation=" + dropoffLocation +
                ", status='" + status + '\'' +
                ", fare=" + fare +
                ", requestTime='" + requestTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}