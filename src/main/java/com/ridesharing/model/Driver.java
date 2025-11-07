package com.ridesharing.model;

public class Driver {
    private String id;
    private String name;
    private String email;
    private String phone;
    private double rating;
    private String vehicleModel;
    private String licensePlate;
    private boolean available;

    // Constructor vacío
    public Driver() {
    }

    // Constructor con parámetros
    public Driver(String id, String name, String email, String phone, 
                  double rating, String vehicleModel, String licensePlate, 
                  boolean available) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.rating = rating;
        this.vehicleModel = vehicleModel;
        this.licensePlate = licensePlate;
        this.available = available;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", rating=" + rating +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", available=" + available +
                '}';
    }
}