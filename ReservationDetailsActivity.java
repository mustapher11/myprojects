package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carhire.asynctasks.ApproveTask;
import com.example.carhire.asynctasks.DriverNamesTask;
import com.example.carhire.asynctasks.FetchAdminReservationDetailsTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.asynctasks.RejectTask;
import com.example.carhire.asynctasks.UpdateCarStatusTask;
import com.example.carhire.interfaces.ManageCarInterface;
import com.example.carhire.interfaces.ManageDriverInterface;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.interfaces.ReservationInterface;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class ReservationDetailsActivity extends AppCompatActivity implements NetworkConnectionInterface, ReservationInterface, ManageDriverInterface, ManageCarInterface {

    ActionBar actionBar;
    ConstraintLayout constraintLayout;
    Button approve, reject;
    Spinner  driver_spinner;
    EditText assigned_driver;
    ProgressDialog progressDialog;
    Snackbar snackbar;
    ImageView imageView;
    TextView reservation_id, client_id, car_name, car_model, plate_number, assign_driver, assignedDriver, status;
    private String type;
    private String userId;
    private String id;
    private String id1;
    private String assignDriverText;
    private String car_number;
    boolean result;
    ArrayList<String> driver1s = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details);

        Intent intent = getIntent();
        type = intent.getStringExtra("type1");
        userId = intent.getStringExtra("id");
        id = intent.getStringExtra("id1");

        Log.d("user_id", userId);

        constraintLayout = findViewById(R.id.constraint_layout);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Reservation Details");

        imageView = findViewById(R.id.car_image);
        reservation_id = findViewById(R.id.reservation_id);
        client_id = findViewById(R.id.client_id);
        car_name = findViewById(R.id.car_name);
        car_model = findViewById(R.id.car_model);
        plate_number = findViewById(R.id.plate_number);
        assign_driver = findViewById(R.id.assign_driver);
        assignedDriver = findViewById(R.id.assigned_driver);
        status = findViewById(R.id.status);

        approve = findViewById(R.id.approve);
        reject = findViewById(R.id.reject);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching reservation details...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Events
        checkReservationType();
        approve.setOnClickListener(v -> checkDriverAssignment());
        reject.setOnClickListener(v -> rejectReservation());
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkManager networkManager = new NetworkManager(this, this);
        networkManager.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();

        }else if (item.getItemId() == R.id.refresh){
            progressDialog.show();
            NetworkManager networkManager = new NetworkManager(this, this);
            networkManager.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkReservationType(){
        if (type != null && type.equalsIgnoreCase("Approved")){
            approve.setEnabled(false);

        }else if (type != null && type.equalsIgnoreCase("Rejected")){
            reject.setEnabled(false);
        }
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    public void assignDriver(){
        View view = LayoutInflater.from(this).inflate(R.layout.activity_assign_driver, null);
        driver_spinner = view.findViewById(R.id.driver_spinner);
        assigned_driver = view.findViewById(R.id.assigned_drivers);
        assigned_driver.setFocusable(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, driver1s);
        driver_spinner.setAdapter(adapter);

        driver_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().equals("Assign Driver")){
                    assigned_driver.getText().clear();

                }else {
                    assigned_driver.setText(parent.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Assign Driver").setMessage("Select driver(s) to assign to client")
                .setView(view).setPositiveButton("Assign", (dialog, which) -> {
                    if (result){
                        if (assigned_driver.getText().toString().trim().isEmpty()){
                            Toast.makeText(this, "Field cannot be empty!", Toast.LENGTH_LONG).show();

                        }else {
                            progressDialog.setMessage("Please wait...");
                            progressDialog.show();
                            ApproveTask approveTask = new ApproveTask(ReservationDetailsActivity.this, ReservationDetailsActivity.this);
                            approveTask.execute("Approved", assigned_driver.getText().toString().trim(), id1);
                        }
                    }else {
                        Toast.makeText(this, "Please check that you have an active internet connection and try again!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void rejectReservation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reject Reservation").setMessage("Are you sure you want to reject the reservation?")
                .setPositiveButton("Reject", (dialog, which) -> {
                    if (result){
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();
                        RejectTask rejectTask = new RejectTask(ReservationDetailsActivity.this, ReservationDetailsActivity.this);
                        rejectTask.execute("Rejected", "None", id1);

                    }else {
                        Toast.makeText(this, "Please check that you have an active internet connection and try again!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void pingResult(boolean result) {
        this.result = result;

        if (result){
            if (userId != null){
                FetchAdminReservationDetailsTask fetchAdminReservationDetailsTask = new FetchAdminReservationDetailsTask(this, this);
                fetchAdminReservationDetailsTask.execute(id, userId);
            }

            DriverNamesTask driverNamesTask = new DriverNamesTask(this, this);
            driverNamesTask.execute();

        }else {
            progressDialog.dismiss();
            showSnackBar();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void reservationDetails1Result(JSONObject JsonObject) {
        if (JsonObject != null) {
            byte[] bytes;
            Bitmap bitmap;

            try {
                bytes = Base64.decode(JsonObject.getString("car_image"), Base64.DEFAULT);
                InputStream inputStream1 = new ByteArrayInputStream(bytes);
                bitmap = BitmapFactory.decodeStream(inputStream1);

                id1 = JsonObject.getString("id");
                assignDriverText = JsonObject.getString("assign_driver");
                car_number = JsonObject.getString("plate_number");

                imageView.setImageBitmap(bitmap);
                reservation_id.setText("#" + JsonObject.getString("id"));
                client_id.setText(JsonObject.getString("user_id"));
                car_name.setText(JsonObject.getString("car_name"));
                car_model.setText(JsonObject.getString("car_model"));
                plate_number.setText(JsonObject.getString("plate_number"));
                assign_driver.setText(JsonObject.getString("assign_driver"));
                assignedDriver.setText(JsonObject.getString("assigned_driver"));
                status.setText(JsonObject.getString("status"));

                progressDialog.dismiss();

            } catch (JSONException e) {
                Log.d("error", e.getMessage());
            }
        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "No results found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void notify(ArrayList<String> driver1s){
        this.driver1s = driver1s;
    }

    @Override
    public void driverNamesResult(@NonNull ArrayList<String> driver1s) {
        if (!driver1s.isEmpty()){
            notify(driver1s);
            progressDialog.dismiss();

        }else {
            Toast.makeText(this, "No results found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSnackBar(){
        snackbar = Snackbar.make(constraintLayout, "Please check your internet connection and try again!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setTextColor(Color.parseColor("#E0E1DD"));
        snackbar.setActionTextColor(Color.parseColor("#E0E1DD"));

        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#3700B3"));

        snackbar.setAction("RETRY", v -> {
                    progressDialog.show();
                    new NetworkManager(ReservationDetailsActivity.this, ReservationDetailsActivity.this).execute();
                }
        );

        snackbar.show();
    }

    @Override
    public void approveResult(String response) {
        if (response != null && response.equalsIgnoreCase("Reservation approved!")){
            UpdateCarStatusTask updateCarStatusTask = new UpdateCarStatusTask(this, this);
            updateCarStatusTask.execute("Unavailable", car_number);

        }else {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateCarStatusResult(String response) {
        if (response != null && response.equalsIgnoreCase("Car unavailable!")){
            FetchAdminReservationDetailsTask fetchAdminReservationDetailsTask = new FetchAdminReservationDetailsTask(this, this);
            fetchAdminReservationDetailsTask.execute(id, userId);
        }
    }

    public void checkDriverAssignment(){
        if (assignDriverText != null && assignDriverText.equalsIgnoreCase("yes")){
            assignDriver();

        }else {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            ApproveTask approveTask = new ApproveTask(ReservationDetailsActivity.this, ReservationDetailsActivity.this);
            approveTask.execute("Approved", "None", id1);
        }
    }

    @Override
    public void rejectResult(String response) {
        if (response != null && response.equalsIgnoreCase("Reservation rejected!")){
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            UpdateCarStatusTask updateCarStatusTask = new UpdateCarStatusTask(this, this);
            updateCarStatusTask.execute("Available", car_number);
        }
    }
}