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
import android.widget.Toast;

import com.example.carhire.adapter.CarAdapter;
import com.example.carhire.asynctasks.AdminCarsTask;
import com.example.carhire.asynctasks.DeleteCarTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.asynctasks.UpdateCarTask;
import com.example.carhire.interfaces.ManageCarInterface;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.models.Car;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

@SuppressLint("StaticFieldLeak")
public class CarsActivity extends AppCompatActivity implements ManageCarInterface, NetworkConnectionInterface {

    ActionBar actionBar;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    Snackbar snackbar;
    ConstraintLayout constraintLayout;
    public static CarsActivity carsActivity;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Car> carArrayList = new ArrayList<>();
    private CarAdapter carAdapter;
    AdminCarsTask adminCarsTask;
    boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Cars");

        carsActivity = this;
        constraintLayout = findViewById(R.id.constraint_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching cars...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        carAdapter = new CarAdapter(this, carArrayList);

        recyclerView = findViewById(R.id.cars_list);
        linearLayoutManager = new LinearLayoutManager(this);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        } else if (item.getItemId() == R.id.refresh) {
            progressDialog.setMessage("Fetching cars...");
            progressDialog.show();
            NetworkManager networkManager = new NetworkManager(this, this);
            networkManager.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fetchCarResult(ArrayList<Car> cars) {
        if (!cars.isEmpty()) {
            carArrayList.clear();
            carAdapter.notify(cars);
            progressDialog.dismiss();

        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "No results!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void pingResult(boolean result) {
        this.result = result;

        if (result) {
            adminCarsTask = new AdminCarsTask(this, this);
            adminCarsTask.execute();

        } else {
            progressDialog.dismiss();
            showSnackBar();
        }
    }

    private void showSnackBar() {
        snackbar = Snackbar.make(constraintLayout, "Please check your internet connection and try again!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setTextColor(Color.parseColor("#E0E1DD"));
        snackbar.setActionTextColor(Color.parseColor("#E0E1DD"));

        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#023E8A"));

        snackbar.setAction("RETRY", v -> {
                    progressDialog.show();
                    new NetworkManager(CarsActivity.this, CarsActivity.this).execute();
                }
        );

        snackbar.show();
    }

    @SuppressLint("InflateParams")
    public void updateCar(String car_name, String car_model, ArrayList<Car> carArrayList, int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.input_plate_number, null);
        EditText cost = view.findViewById(R.id.plate_number);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter new cost")
                .setMessage("Enter new cost for " + car_name + " " + car_model)
                .setView(view)
                .setPositiveButton("Update", (dialog, which) -> {
                    if (result){
                        if (cost.getText().toString().trim().replace(" ", "").isEmpty()) {
                            Toast.makeText(this, "Field cannot be empty!", Toast.LENGTH_LONG).show();

                        }else if (cost.getText().toString().trim().replace(" ", "").length() > 10){
                            Toast.makeText(this, "Characters length cannot be more than 10!", Toast.LENGTH_LONG).show();

                        }else {
                            progressDialog.setMessage("Updating car...");
                            progressDialog.show();
                            UpdateCarTask updateCarTask = new UpdateCarTask(this, this);
                            updateCarTask.execute(cost.getText().toString().trim().replace(" ", ""), carArrayList.get(position).getCar_number());
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
    public void updateCarResult(String response) {
        if (response != null) {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            adminCarsTask = new AdminCarsTask(this, this);
            adminCarsTask.execute();
        }
    }

    public void confirmation(String car_name, String car_model, String plate_number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Are you sure you want to delete " + car_name + " " + car_model + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (result){
                        progressDialog.setMessage("Deleting car");
                        progressDialog.show();
                        DeleteCarTask deleteCarTask = new DeleteCarTask(this, this);
                        deleteCarTask.execute(plate_number);

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Please check that you have an active internet connection!", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("No", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void deleteCarResult(String response) {
        if (response != null){
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            adminCarsTask = new AdminCarsTask(this, this);
            adminCarsTask.execute();
        }
    }
}