package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.carhire.adapter.UserAdapter;
import com.example.carhire.asynctasks.FetchCustomerTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.interfaces.UserProfileInterface;
import com.example.carhire.models.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class CustomersActivity extends AppCompatActivity implements UserProfileInterface, NetworkConnectionInterface {

    ActionBar actionBar;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    ConstraintLayout constraintLayout;
    Snackbar snackbar;

    ArrayList<User> users = new ArrayList<>();
    FetchCustomerTask fetchCustomerTask;

    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Customers");

        constraintLayout = findViewById(R.id.constraint_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching user details...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        userAdapter = new UserAdapter(this, users);

        recyclerView = findViewById(R.id.customer_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userAdapter);
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

    @Override
    public void fetchCustomerResult(@NonNull ArrayList<User> users) {
        if (!users.isEmpty()){
            this.users.clear();
            progressDialog.dismiss();
            userAdapter.notify(users);

        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "No results found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void pingResult(boolean result) {
        if (result){
            fetchCustomerTask = new FetchCustomerTask(CustomersActivity.this, CustomersActivity.this);
            fetchCustomerTask.execute();

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
                    NetworkManager networkManager = new NetworkManager(CustomersActivity.this, CustomersActivity.this);
                    networkManager.execute();
                }
        );
        snackbar.show();
    }
}