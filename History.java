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

import com.example.carhire.adapter.HistoryAdapter;
import com.example.carhire.asynctasks.FetchIdTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.interfaces.ReservationInterface;
import com.example.carhire.models.HiringHistory;
import com.example.carhire.sessions.UserSessionManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class History extends AppCompatActivity implements NetworkConnectionInterface, ReservationInterface {

    public static History history;
    ActionBar actionBar;
    ProgressDialog progressDialog;
    RecyclerView history_list;
    ConstraintLayout constraintLayout;
    Snackbar snackbar;

    HistoryAdapter historyAdapter;
    ArrayList<HiringHistory> hiringHistories = new ArrayList<>();

    FetchIdTask fetchIdTask;
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        history = this;

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Hiring History");

        constraintLayout = findViewById(R.id.constraint_layout);

        userSessionManager = new UserSessionManager(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching reservations...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        historyAdapter = new HistoryAdapter(this, hiringHistories);

        history_list = findViewById(R.id.history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        history_list.setLayoutManager(linearLayoutManager);
        history_list.setAdapter(historyAdapter);

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item){

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
            fetchIdTask = new FetchIdTask(this, this);
            fetchIdTask.execute(userSessionManager.getUserData());

        }else {
            progressDialog.dismiss();
            showSnackBar();
        }
    }

    @Override
    public void fetchReservationHistoryResult(ArrayList<HiringHistory> hiringHistoryArrayList) {
        if (!hiringHistoryArrayList.isEmpty()){
            progressDialog.dismiss();
            historyAdapter.notify(hiringHistoryArrayList);

        }else {
            progressDialog.dismiss();
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
                    new NetworkManager(History.this, History.this).execute();
                }
        );

        snackbar.show();
    }

    public void moveToHistoryDetails(ArrayList<HiringHistory> hiringHistories, int position){
        Intent intent = new Intent(this, HistoryDetailsActivity.class);
        intent.putExtra("id", hiringHistories.get(position).getId());
        startActivity(intent);
    }
}