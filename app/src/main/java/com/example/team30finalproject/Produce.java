package com.example.team30finalproject;

public class Produce {

    public String name;
    public double price;
    public int quantity;
    public String imageFileName;
    public double latitude;
    public double longitude;
    public String streetAddress;

    public Produce(String name, double price, int quantity, String imageFileName,
                   double latitude, double longitude, String streetAddress) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageFileName = imageFileName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.streetAddress = streetAddress;
    }
}