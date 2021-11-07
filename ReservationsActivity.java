package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.carhire.adapter.ReservationAdapter;
import com.example.carhire.asynctasks.FetchReservationTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.interfaces.ReservationInterface;
import com.example.carhire.models.Reservation;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class ReservationsActivity extends AppCompatActivity implements NetworkConnectionInterface, ReservationInterface {

    ActionBar actionBar;
    RecyclerView reservation_list;
    ProgressDialog progressDialog;
    ConstraintLayout constraintLayout;
    Snackbar snackbar;

    String type;
    ReservationAdapter reservationAdapter;
    ArrayList<Reservation> reservations = new ArrayList<>();

    FetchReservationTask fetchReservationTask;
    public static ReservationsActivity reservationsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        reservationsActivity = this;

        Intent intent1 = getIntent();
        type = intent1.getStringExtra("type");

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(type + " Reservations");

        constraintLayout = findViewById(R.id.constraint_layout);

        reservationAdapter = new ReservationAdapter(this, reservations);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching reservations...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        reservation_list = findViewById(R.id.reservations_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        reservation_list.setLayoutManager(linearLayoutManager);
        reservation_list.setAdapter(reservationAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new NetworkManager(this, this).execute();
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

    @Override
    public void pingResult(boolean result) {
        if (result){
            fetchReservationTask = new FetchReservationTask(this, this);
            fetchReservationTask.execute(type);

        }else {
            progressDialog.dismiss();
            showSnackBar();
        }
    }

    @Override
    public void fetchReservationResult(ArrayList<Reservation> reservations) {
        if (!reservations.isEmpty()){
            progressDialog.dismiss();
            this.reservations.clear();
            reservationAdapter.notify(reservations);

        }else{
            progressDialog.dismiss();
            Toast.makeText(this, "No results!", Toast.LENGTH_SHORT).show();
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
                    new NetworkManager(ReservationsActivity.this, ReservationsActivity.this).execute();
                }
        );
        snackbar.show();
    }

    public void moveToReservationDetails(@NonNull ArrayList<Reservation> reservations, int position){
        Intent intent = new Intent(this, ReservationDetailsActivity.class);
        intent.putExtra("id1", reservations.get(position).getId());
        intent.putExtra("id", reservations.get(position).getUserId());
        intent.putExtra("type1", type);
        startActivity(intent);
    }
}