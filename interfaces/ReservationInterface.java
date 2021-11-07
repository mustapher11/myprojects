package com.example.carhire.interfaces;

import com.example.carhire.models.HiringHistory;
import com.example.carhire.models.Reservation;
import org.json.JSONObject;

import java.util.ArrayList;

public interface ReservationInterface {

    default void getReservationResult(String response){}

    default void fetchReservationResult(ArrayList<Reservation> reservations){}

    default void fetchReservationHistoryResult(ArrayList<HiringHistory> hiringHistoryArrayList){}

    default void reservationDetailsResult(JSONObject JsonObject){}

    default void updatePaymentResult(String response){}

    default void reservationDetails1Result(JSONObject JsonObject){}

    default void approveResult(String response){}

    default void rejectResult(String response){}

}