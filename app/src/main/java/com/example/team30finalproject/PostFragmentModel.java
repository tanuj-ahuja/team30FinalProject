package com.example.team30finalproject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostFragmentModel implements Comparable<PostFragmentModel>{
    String name, streetAddress, time;
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

    public PostFragmentModel(String name, double price, int quantity, double latitude,
                              double longitude, String streetAddress, String time) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.streetAddress = streetAddress;
        this.time = time;
    }

    @Override
    public int compareTo(PostFragmentModel o) {
        Date d1=new Date();
        Date d2=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        if(this.time == null || o.time ==null){
            return 1;
        }
        try {
            d1 = sdf.parse(this.time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            d2 = sdf.parse(o.time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(d1.compareTo(d2)>0){
            return 1;
        } else{
            return -1;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
