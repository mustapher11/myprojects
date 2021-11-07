package com.example.carhire.interfaces;

import com.example.carhire.models.Driver;

import java.util.ArrayList;
import java.util.List;

public interface ManageDriverInterface {

    default void addDriverResult(String response){}

    default void searchDriverResult(List<String> driverDetails){}

    default void updateDriverResult(String response){}

    default void fetchDriverResult(ArrayList<Driver> drivers){}

    default void deleteDriverResult(String response){}

    default void driverNamesResult(ArrayList<String> driver1s){}

}