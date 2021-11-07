package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.carhire.asynctasks.DetailsTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.asynctasks.UpdateProfileTask;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.interfaces.UserProfileInterface;
import com.example.carhire.sessions.UserSessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements UserProfileInterface, NetworkConnectionInterface {

    ActionBar actionBar;
    TextInputEditText firstName, lastName, phone, password, password1, password2;
    TextInputLayout layout1, layout2, layout3, layout4, layout5, layout6;
    Button button;
    ProgressDialog progressDialog;
    UserSessionManager userSessionManager;

    boolean result;
    DetailsTask detailsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("My Profile");

        firstName = findViewById(R.id.update_firstName);
        lastName = findViewById(R.id.update_lastName);
        phone = findViewById(R.id.update_phone);
        password = findViewById(R.id.password);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
        button = findViewById(R.id.button_update);

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);
        layout5 = findViewById(R.id.layout5);
        layout6 = findViewById(R.id.layout6);

        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Fetching details...");
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();

        button.setOnClickListener(v -> updateProfile());
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
            progressDialog.setMessage("Fetching details...");
            progressDialog.show();
            NetworkManager networkManager = new NetworkManager(this,this);
            networkManager.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getUserData(@NonNull List<String> result) {
        if (!result.isEmpty()){
            progressDialog.dismiss();
            firstName.setText(result.get(0));
            lastName.setText(result.get(1));
            phone.setText(result.get(2));

        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "No results found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void pingResult(boolean result) {
        this.result = result;
        Log.d("pingResult", String.valueOf(this.result));

        checkConnectivity(result);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkManager networkManager = new NetworkManager(this,this);
        networkManager.execute();
    }

    public void checkConnectivity(boolean result){
        userSessionManager = new UserSessionManager(this);
        String id = userSessionManager.getUserData();

        if (result){
            detailsTask = new DetailsTask(ProfileActivity.this, ProfileActivity.this);
            detailsTask.execute(id);

        }else {
            Toast.makeText(this, "Details cannot be fetched without an active internet connection!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    public void updateProfile(){
        String first_name = Objects.requireNonNull(firstName.getText()).toString().trim();
        String last_name = Objects.requireNonNull(lastName.getText()).toString().trim();
        String phone_number = Objects.requireNonNull(phone.getText()).toString().trim();
        String passwordText = Objects.requireNonNull(password.getText()).toString().trim();
        String password1Text = Objects.requireNonNull(password1.getText()).toString().trim();
        String password2Text = Objects.requireNonNull(password2.getText()).toString().trim();
        userSessionManager = new UserSessionManager(this);

        if (result){
            if (first_name.isEmpty() && last_name.isEmpty() && phone_number.isEmpty() && passwordText.isEmpty() && password1Text.isEmpty() && password2Text.isEmpty()){
                layout1.setError("Field cannot be empty!");
                layout2.setError("Field cannot be empty!");
                layout3.setError("Field cannot be empty!");
                layout4.setError("Field cannot be empty!");
                layout5.setError("Field cannot be empty!");
                layout6.setError("Field cannot be empty!");

            }else if (first_name.isEmpty()){
                layout1.setError("Field cannot be empty!");

            }else if (last_name.isEmpty()){
                layout2.setError("Field cannot be empty!");

            }else if (phone_number.isEmpty()) {
                layout3.setError("Field cannot be empty!");

            }else if (passwordText.isEmpty()){
                layout4.setError("Field cannot be empty!");

            }else if (password1Text.isEmpty()) {
                layout5.setError("Field cannot be empty!");

            }else if (password2Text.isEmpty()){
                layout6.setError("Field cannot be empty!");

            }else if (first_name.length() > 20){
                layout1.setError("Name cannot exceed 20 characters!");

            }else if (last_name.length() > 20){
                layout2.setError("Name cannot exceed 20 characters!");

            }else if (phone_number.length() > 10){
                layout3.setError("Phone number cannot exceed 10 characters!");

            }else if (passwordText.length() > 15){
                layout4.setError("Password cannot exceed 15 characters!");

            }else if (password1Text.length() > 15){
                layout5.setError("Password cannot exceed 15 characters!");

            }else if (password2Text.length() > 15){
                layout6.setError("Password cannot exceed 15 characters!");

            }else if (!password1Text.equals(password2Text)){
                layout5.setError("Passwords not matching!");
                layout6.setError("Passwords not matching!");

            }else {
                progressDialog.setMessage("Updating profile...");
                progressDialog.show();
                UpdateProfileTask updateProfileTask = new UpdateProfileTask(this, this);
                updateProfileTask.execute(first_name, last_name, phone_number, password1Text, userSessionManager.getUserData(), passwordText);
            }

        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please ensure that you have an active internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateProfileResult(String response) {
        if (response != null){
            progressDialog.dismiss();
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(password.getText()).clear();
            Objects.requireNonNull(password1.getText()).clear();
            Objects.requireNonNull(password2.getText()).clear();
        }
    }
}