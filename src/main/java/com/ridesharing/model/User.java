package com.ridesharing.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private double rating;

    // Constructor vacío
    public User() {
    }

    // Constructor con parámetros
    public User(String id, String name, String email, String phone, double rating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.rating = rating;
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", rating=" + rating +
                '}';
    }
}