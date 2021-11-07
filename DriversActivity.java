package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.carhire.adapter.DriverAdapter;
import com.example.carhire.asynctasks.DeleteDriverTask;
import com.example.carhire.asynctasks.FetchDriverTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.asynctasks.UpdateDriverTask;
import com.example.carhire.interfaces.ManageDriverInterface;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.models.Driver;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class DriversActivity extends AppCompatActivity implements ManageDriverInterface, NetworkConnectionInterface {

    ActionBar actionBar;
    RadioButton update, delete;
    RecyclerView recyclerView;
    ConstraintLayout constraintLayout;
    ProgressDialog progressDialog;
    Snackbar snackbar;

    ArrayList<Driver> driverArrayList = new ArrayList<>();
    DriverAdapter driverAdapter;
    FetchDriverTask fetchDriverTask;
    boolean result;

    @SuppressLint("StaticFieldLeak")
    public static DriversActivity driversActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Drivers");

        driversActivity = this;

        recyclerView = findViewById(R.id.drivers_list);
        constraintLayout = findViewById(R.id.constraint_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching drivers' details...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        driverAdapter = new DriverAdapter(this, driverArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(driverAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkManager networkManager = new NetworkManager(this, this);
        networkManager.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();

        }else if (item.getItemId() == R.id.refresh){
            progressDialog.setMessage("Fetching drivers' details...");
            progressDialog.show();
            NetworkManager networkManager = new NetworkManager(this, this);
            networkManager.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    public void manageDriver(String first_name, String last_name, String id){
        View view = LayoutInflater.from(this).inflate(R.layout.manage, null);
        update = view.findViewById(R.id.update);
        delete = view.findViewById(R.id.delete);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Driver").setView(view)
                .setMessage("What do you want to do to driver " + first_name + " " + last_name + "?")
                .setPositiveButton("Ok", (dialog, which) -> {
                    if (update.isChecked()){
                        updateDriver(first_name, last_name, id);

                    }else {
                        confirmation(first_name, last_name, id);
                    }
                })
                .setNegativeButton("cancel", null)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("InflateParams")
    public void updateDriver(String first_name, String last_name, String id){
        View view = LayoutInflater.from(this).inflate(R.layout.input_phone_number, null);
        EditText phoneNumber = view.findViewById(R.id.phone_number);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter new cost")
                .setMessage("Enter new phone number for " + first_name + " " + last_name)
                .setView(view)
                .setPositiveButton("Update", (dialog, which) -> {
                    if (result){
                        if (phoneNumber.getText().toString().trim().replace(" ", "").isEmpty()){
                            Toast.makeText(this, "Field cannot be empty!", Toast.LENGTH_SHORT).show();

                        }else if (phoneNumber.getText().toString().trim().replace(" ", "").length() > 10){
                            Toast.makeText(this, "Characters length cannot be more than 10!", Toast.LENGTH_SHORT).show();

                        }else {
                            progressDialog.setMessage("Updating driver details...");
                            progressDialog.show();
                            UpdateDriverTask updateDriverTask = new UpdateDriverTask(this, this);
                            updateDriverTask.execute(phoneNumber.getText().toString().replace(" ", "").trim(), id);
                        }

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Please check that you have an active internet connection!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void fetchDriverResult(@NonNull ArrayList<Driver> drivers) {
        if (!drivers.isEmpty()){
            driverArrayList.clear();
            progressDialog.dismiss();
            driverAdapter.notify(drivers);

        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "No results found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void pingResult(boolean result) {
        this.result = result;

        if (result){
            fetchDriverTask = new FetchDriverTask(DriversActivity.this, DriversActivity.this);
            fetchDriverTask.execute();

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
        snackBarView.setBackgroundColor(Color.parseColor("#023E8A"));

        snackbar.setAction("RETRY", v -> {
                    progressDialog.show();
                    new NetworkManager(DriversActivity.this, DriversActivity.this).execute();
                }
        );

        snackbar.show();
    }

    @Override
    public void updateDriverResult(String response) {
        if (response != null){
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            fetchDriverTask = new FetchDriverTask(DriversActivity.this, DriversActivity.this);
            fetchDriverTask.execute();
        }
    }

    public void confirmation(String first_name, String last_name, String id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Are you sure you want to delete  driver " + first_name + " " + last_name + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (result){
                        progressDialog.setMessage("Deleting driver");
                        progressDialog.show();
                        DeleteDriverTask deleteDriverTask = new DeleteDriverTask(this, this);
                        deleteDriverTask.execute(id);

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Please check that you have an active internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void deleteDriverResult(String response) {
        if (response != null){
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            fetchDriverTask = new FetchDriverTask(DriversActivity.this, DriversActivity.this);
            fetchDriverTask.execute();
        }
    }
}