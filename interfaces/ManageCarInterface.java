package com.example.carhire.interfaces;

import com.example.carhire.models.Car;

import java.util.ArrayList;

public interface ManageCarInterface {

    default void addCarResult(String result){}

    default void fetchCarResult(ArrayList<Car> cars){}

    default void updateCarResult(String response){}

    default void deleteCarResult(String response){}

    default void updateCarStatusResult(String response){}
}