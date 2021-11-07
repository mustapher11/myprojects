package com.example.carhire.models;

public class User {

    String firstName, lastName, idNumber, phoneNumber;

    public User(String firstName, String lastName, String idNumber, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.idNumber = idNumber;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}