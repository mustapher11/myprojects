package com.example.carhire.interfaces;

import com.example.carhire.models.User;

import java.util.ArrayList;
import java.util.List;

public interface UserProfileInterface {

    default void getUserData(List<String> result) {

    }

    default void fetchCustomerResult(ArrayList<User> users){

    }

    default void updateProfileResult(String response){

    }
}