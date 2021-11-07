package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.asynctasks.RegisterTask;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.interfaces.RegisterInterface;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements RegisterInterface, NetworkConnectionInterface {

    ActionBar actionBar;
    TextInputEditText firstName, lastName, id_number, phone, password, password2;
    TextInputLayout layout1, layout2, layout3, layout4, layout5, layout6;
    Button register_button;
    ProgressDialog progressDialog;
    boolean result;
    String result1;

    RegisterTask registerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Register");

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        id_number = findViewById(R.id.id_number);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        register_button = findViewById(R.id.register_button);

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);
        layout5 = findViewById(R.id.layout5);
        layout6 = findViewById(R.id.layout6);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Events Handling.
        register_button.setOnClickListener(v -> register());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void register(){
        String firstName_text = Objects.requireNonNull(firstName.getText()).toString();
        String lastName_text = Objects.requireNonNull(lastName.getText()).toString();
        String idNumber_text = Objects.requireNonNull(id_number.getText()).toString().replace(" ", "");
        String phone_text = Objects.requireNonNull(phone.getText()).toString().replace(" ", "");
        String password_text = Objects.requireNonNull(password.getText()).toString().replace(" ", "");
        String password2_text = Objects.requireNonNull(password2.getText()).toString().replace(" ", "");

        if (result){
            if (firstName_text.trim().isEmpty() && lastName_text.trim().isEmpty() && idNumber_text.trim().isEmpty()
                    && phone_text.trim().isEmpty() && password_text.trim().isEmpty() && password2_text.trim().isEmpty()){
                layout1.setError("This field must be filled!");
                layout2.setError("This field must be filled!");
                layout3.setError("This field must be filled!");
                layout4.setError("This field must be filled!");
                layout5.setError("This field must be filled!");
                layout6.setError("This field must be filled!");

            }else if (firstName_text.trim().isEmpty()){
                layout1.setError("This field must be filled!");

            }else if (lastName_text.trim().isEmpty()){
                layout2.setError("This field must be filled!");

            }else if (idNumber_text.trim().isEmpty()){
                layout3.setError("This field must be filled!");

            }else if (phone_text.trim().isEmpty()){
                layout4.setError("This field must be filled!");

            }else if (password_text.trim().isEmpty()){
                layout5.setError("This field must be filled!");

            }else if (password2_text.trim().isEmpty()){
                layout6.setError("This field must be filled!");

            }else if (firstName_text.trim().length() > 20){
                layout1.setError("Name cannot exceed 20 characters!");

            }else if (lastName_text.trim().length() > 20){
                layout2.setError("Name cannot exceed 20 characters!");

            }else if (idNumber_text.trim().length() > 10){
                layout3.setError("ID number cannot exceed 10 characters!");

            }else if (phone_text.trim().length() > 10){
                layout4.setError("Phone number cannot exceed 10 characters!");

            }else if (password_text.trim().length() > 15){
                layout5.setError("Password cannot exceed 15 characters!");

            }else if (password2_text.trim().length() > 15){
                layout6.setError("Password cannot exceed 15 characters!");

            }else if (!password_text.trim().equals(password2_text.trim())){
                layout5.setError("Passwords not matching!");
                layout6.setError("Passwords not matching!");
            }

            else {
                registerTask = new RegisterTask(this, this, progressDialog, firstName, lastName, id_number, phone, password);
                registerTask.execute();
            }

        }else {
            Toast.makeText(this, "Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
        }
    }

    public void moveToLogIn(String response){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("message", response);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void registerResult(String result) {
        result1 = result;
        if (result != null && result.equalsIgnoreCase("Registration Successful!")){
            progressDialog.dismiss();
            moveToLogIn(result);
//            Toast.makeText(this, result + " Proceed to Log In.", Toast.LENGTH_LONG).show();

        }else {
            progressDialog.dismiss();
            Objects.requireNonNull(id_number.getText()).clear();
            Objects.requireNonNull(password.getText()).clear();
            Objects.requireNonNull(password2.getText()).clear();
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void pingResult(boolean result) {
        this.result = result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkManager networkManager = new NetworkManager(this, this);
        networkManager.execute();
    }
}