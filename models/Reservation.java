package com.example.carhire.models;

import android.graphics.Bitmap;

public class Reservation {

    Bitmap image;
    String id, userId, cost, code, date, time;

    public Reservation(String id,Bitmap image, String userId, String cost, String code, String date) {
        this.id = id;
        this.image = image;
        this.userId = userId;
        this.cost = cost;
        this.code = code;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getUserId() {
        return userId;
    }

    public String getCost() {
        return cost;
    }

    public String getCode() {
        return code;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}