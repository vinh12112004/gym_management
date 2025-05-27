package com.example.gym.model;

import java.time.LocalDate;

public class Package {
    private long id;
    private String name;
    private double price;
    private LocalDate expiryDate;

    public Package() {}
    public Package(long id, String name, double price, LocalDate expiryDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.expiryDate = expiryDate;
    }
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}