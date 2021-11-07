package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Objects;

public class CarDetails extends AppCompatActivity {

    ActionBar actionBar;
    TextView cost;
    EditText days, car;
    Button hire_button;
    RadioButton yes, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Car Details");

        cost = findViewById(R.id.textView11);
        hire_button = findViewById(R.id.hire_button);

        //Events Handling.
        hire_button.setOnClickListener(v -> enterCars());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("InflateParams")
    public void enterDays(){
        View view = LayoutInflater.from(this).inflate(R.layout.input_days, null);
        days = view.findViewById(R.id.days);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Number of Days")
                .setMessage("Enter number of days you wish to hire the car")
                .setView(view)
                .setPositiveButton("Ok", (dialog, which) -> {
                    if (days.getText().toString().isEmpty()){
                        days.setError("Field cannot be empty!");

                    }else if (days.getText().toString().equals("0")){
                        days.getText().clear();
                        days.setError("Input cannot be 0");

                    }else {
                        assignDriver();
                    }
                    })
                .setNegativeButton("Cancel", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("InflateParams")
    public void enterCars(){
        View view = LayoutInflater.from(this).inflate(R.layout.input_cars, null);
        car = view.findViewById(R.id.cars);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Number of cars")
                .setMessage("Enter number of cars you wish to hire")
                .setView(view).setPositiveButton("Ok", (dialog, which) -> {
                    if (car.getText().toString().isEmpty()){
                        car.setError("Filed cannot be empty!!");

                    }else if (car.getText().toString().equals("0")){
                        car.getText().clear();
                        car.setError("Input cannot be zero");

                    }else {
                        enterDays();
                    }
                    }).setNegativeButton("Cancel", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("InflateParams")
    public void assignDriver(){
        View view = LayoutInflater.from(this).inflate(R.layout.assign_driver, null);
        yes = view.findViewById(R.id.yes);
        no = view.findViewById(R.id.no);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Assign Driver").setMessage("Do you wish to be assigned a driver?")
                .setView(view).setPositiveButton("Ok", (dialog, which) -> confirmation())
                .setNegativeButton("Cancel", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void confirmation(){
        String cost_text = cost.getText().toString().trim();
        String days_text = days.getText().toString().trim();
        String cars_text = car.getText().toString().trim();

        String total_cost = String.valueOf(Integer.parseInt(cost_text) * Integer.parseInt(days_text) * Integer.parseInt(cars_text));

        String message;

        if (yes.isChecked()){
            message = "Confirm reservation of voxy car for Kshs." + total_cost + " plus a driver?";
        }else {
            message = "Confirm reservation of voxy car for Kshs." + total_cost + "?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Reservation")
                .setMessage(message)
                .setPositiveButton("Hire", (dialog, which) -> {
                    Intent intent = new Intent(CarDetails.this, PaymentActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}