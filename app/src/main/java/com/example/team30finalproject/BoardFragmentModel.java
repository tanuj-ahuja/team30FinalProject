package com.example.team30finalproject;

public class BoardFragmentModel implements Comparable<BoardFragmentModel>{
    String name, streetAddress, fileName;
    int quantity;
    double price, latitude, longitude, distance;

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public BoardFragmentModel(String name, double price, int quantity, double latitude,
                              double longitude, String streetAddress, double distance, String fileName) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.streetAddress = streetAddress;
        this.distance = distance;
        this.fileName = fileName;
    }

    @Override
    public int compareTo(BoardFragmentModel o) {
        return this.distance > o.distance ? 1 : (this.distance < o.distance ? -1 : 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
