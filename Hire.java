package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carhire.adapter.CarAdapter;
import com.example.carhire.asynctasks.AddReservationTask;
import com.example.carhire.asynctasks.CarsTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.asynctasks.UpdateCarStatusTask;
import com.example.carhire.interfaces.ManageCarInterface;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.interfaces.ReservationInterface;
import com.example.carhire.models.Car;
import com.example.carhire.sessions.UserSessionManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class Hire extends AppCompatActivity implements ManageCarInterface, NetworkConnectionInterface, ReservationInterface {

    ActionBar actionBar;
    RecyclerView recyclerView;
    RadioButton yes, no;
    ProgressDialog progressDialog;
    Snackbar snackbar;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView phone, email;
    ConstraintLayout constraintLayout;
    UserSessionManager userSessionManager;

    @SuppressLint("StaticFieldLeak")
    public static Hire hireActivity;

    ArrayList<Car> carArrayList = new ArrayList<>();
    CarAdapter carAdapter;
    AddReservationTask addReservationTask;

    String radioButtonText, plate_number;
    boolean result;

    RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            radioButton = group.findViewById(checkedId);
            radioButtonText = radioButton.getText().toString();
        }
    };

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Hire");

        hireActivity = this;

        constraintLayout = findViewById(R.id.constraint_layout);
        recyclerView = findViewById(R.id.cars);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching cars...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        carAdapter = new CarAdapter(this, carArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(carAdapter);

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
        menuInflater.inflate(R.menu.hire_section, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();

        }else if (item.getItemId() == R.id.logout){
            userSessionManager = new UserSessionManager(this);
            userSessionManager.removeSession();
            moveToLogIn();

        }else if (item.getItemId() == R.id.profile){
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);

        }else if (item.getItemId() == R.id.hires){
            item.setIntent(new Intent(this, History.class));

        }else if (item.getItemId() == R.id.payment){
            item.setIntent(new Intent(this, PaymentActivity.class));

        }else if (item.getItemId() == R.id.contact){
            contactUs();

        }else if (item.getItemId() == R.id.refresh){
            progressDialog.show();
            NetworkManager networkManager = new NetworkManager(this, this);
            networkManager.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    public void moveToLogIn(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    public void assignDriver(String carImage, String carName, String carModel, String plate_number, String cost){
        View view = LayoutInflater.from(this).inflate(R.layout.assign_driver, null);
        radioGroup = view.findViewById(R.id.selection);
        radioGroup.setOnCheckedChangeListener(listener);

        yes = view.findViewById(R.id.yes);
        no = view.findViewById(R.id.no);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Assign Driver").setMessage("Do you wish to be assigned a driver?")
                .setView(view).setPositiveButton("Ok", (dialog, which) -> confirmation(carImage, carName, carModel, plate_number, cost))
                .setNegativeButton("Cancel", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void confirmation(String carImage, String car_name, String car_model, String plate_number, String car_cost){
        String message;

        if (yes.isChecked()){
            message = "Confirm reservation of " + car_name + " " + car_model + " plus a driver for Kshs." + car_cost + "?";

        }else {
            message = "Confirm reservation of " + car_name + " " + car_model + " for Kshs." + car_cost + "?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Reservation")
                .setMessage(message)
                .setPositiveButton("Hire", (dialog, which) -> addReservation(carImage, car_name, car_model, plate_number, car_cost))
                .setNegativeButton("Cancel", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addReservation(String carImage, String carName, String carModel, String plate_number, String cost){
        userSessionManager = new UserSessionManager(this);
        String userId = userSessionManager.getUserData();
        this.plate_number = plate_number;

        if (result){
            progressDialog.setMessage("Making reservation...");
            progressDialog.show();
            addReservationTask = new AddReservationTask(Hire.this, Hire.this);
            addReservationTask.execute(userId, carImage, carName, carModel, plate_number, cost, radioButtonText);

        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void fetchCarResult(@NonNull ArrayList<Car> cars) {
        if (!cars.isEmpty()){
            carArrayList.clear();
            carAdapter.notify(cars);
            progressDialog.dismiss();

        }else{
            progressDialog.dismiss();
            Toast.makeText(this, "No results!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void pingResult(boolean result) {
        this.result = result;
        if (result) {
            CarsTask carsTask = new CarsTask(this, this);
            carsTask.execute();

        }else {
            progressDialog.dismiss();
            showSnackBar();
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
                    NetworkManager networkManager = new NetworkManager(Hire.this, Hire.this);
                    networkManager.execute();
                }
        );

        snackbar.show();
    }

    @Override
    public void getReservationResult(String response) {
        if (response != null && response.equals("0")){
            UpdateCarStatusTask updateCarStatusTask = new UpdateCarStatusTask(this, this);
            updateCarStatusTask.execute("Unavailable", plate_number);

        }else {
            progressDialog.dismiss();
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
            Log.d("reservation response", response);
        }
    }

    public void contactUs(){
        View view = LayoutInflater.from(this).inflate(R.layout.activity_contact, null);
        phone = view.findViewById(R.id.call_number);
        email = view.findViewById(R.id.email);

        phone.setOnClickListener(v -> openDialler());
        email.setOnClickListener(v -> openEmail());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view)
                .setPositiveButton("Close", null)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openDialler(){
        String phoneNumber = phone.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    public void openEmail(){
        String emailText = email.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailText});
        startActivity(intent);
    }

    @Override
    public void updateCarStatusResult(String response) {
        if (response != null && response.equalsIgnoreCase("Car unavailable!")){
            progressDialog.dismiss();
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
        }
    }
}