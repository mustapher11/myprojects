package com.example.carhire.models;

import android.graphics.Bitmap;

public class Car {

    private String car_name, car_model, car_number, hiring_cost;
    Bitmap bitmap;

    public Car(Bitmap bitmap, String car_name, String car_model, String car_number, String hiring_cost) {
        this.car_name = car_name;
        this.car_model = car_model;
        this.car_number = car_number;
        this.hiring_cost = hiring_cost;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getCar_name() {
        return car_name;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getHiring_cost() {
        return hiring_cost;
    }

    public void setHiring_cost(String hiring_cost) {
        this.hiring_cost = hiring_cost;
    }
}
