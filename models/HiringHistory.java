package com.example.carhire.models;

public class HiringHistory {

    String id, status, time;

    public HiringHistory(String id, String status, String time) {
        this.id = id;
        this.status = status;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }
}