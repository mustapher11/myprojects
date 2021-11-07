package com.example.carhire.models;

import android.graphics.Bitmap;

public class ReservationDetails {

    Bitmap carImage;
    String id, carName, carModel, cost, paymentCode, plateNumber, status;

    public ReservationDetails(String id, Bitmap carImage, String carName, String carModel, String cost, String paymentCode, String plateNumber, String status) {
        this.carImage = carImage;
        this.id = id;
        this.carName = carName;
        this.carModel = carModel;
        this.cost = cost;
        this.paymentCode = paymentCode;
        this.plateNumber = plateNumber;
        this.status = status;
    }

    public Bitmap getCarImage() {
        return carImage;
    }

    public String getId() {
        return id;
    }

    public String getCarName() {
        return carName;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getCost() {
        return cost;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getStatus() {
        return status;
    }
}