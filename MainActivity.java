package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carhire.asynctasks.LoginTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.interfaces.LogInInterface;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.sessions.AdminSession;
import com.example.carhire.sessions.UserSessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LogInInterface, NetworkConnectionInterface {

    ActionBar actionBar;
    TextInputEditText id, password;
    TextInputLayout layout1, layout2;
    Button login;
    TextView register_page, register_page1;
    ProgressDialog progressDialog;
    UserSessionManager userSessionManager;
    AdminSession adminSession;
    LoginTask loginTask;
    String message = null;

    boolean result;
    RegisterActivity registerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Log In");

        registerActivity = new RegisterActivity();

        Intent intent = getIntent();
        message = intent.getStringExtra("message");

        id = findViewById(R.id.id_number);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register_page = findViewById(R.id.text_register);
        register_page1 = findViewById(R.id.text_register1);

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Events Handling.
        register_page.setOnClickListener(v -> moveToRegister());
        register_page1.setOnClickListener(v -> moveToRegister());
        login.setOnClickListener(v -> logInButton());
        toastMessage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSession();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void moveToRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void logInButton(){
        String id_text = Objects.requireNonNull(id.getText()).toString();
        String password_text = Objects.requireNonNull(password.getText()).toString();

        if (result){
            if (id_text.trim().isEmpty() && password_text.trim().isEmpty()){
                layout1.setError("Field cannot be empty!");
                layout2.setError("Field cannot be empty!");

            }else if (id_text.trim().isEmpty()){
                layout1.setError("Field cannot be empty!");

            }else if (password_text.trim().isEmpty()){
                layout2.setError("Field cannot be empty!");

            }else if (id_text.trim().length() > 10){
                layout1.setError("Characters length cannot be more than 10!");

            }else if (password_text.trim().length() > 15){
                layout2.setError("Characters length cannot be more than 15!");

            }else {
                loginTask = new LoginTask(this, this, id, password, progressDialog);
                loginTask.execute();
            }

        }else {
            Toast.makeText(this, "Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
        }
    }

    public void moveToHire(){
        Intent intent = new Intent(MainActivity.this, Hire.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void checkSession(){
        userSessionManager = new UserSessionManager(this);
        String user_id = userSessionManager.getUserData();

        adminSession = new AdminSession(this);
        String admin_id = adminSession.getAdminData();

        if (!user_id.equals("")){
            moveToHire();

        }else if (!admin_id.equals("")){
            moveToAdmin();
        }
    }

    public void moveToAdmin(){
        Intent intent = new Intent(MainActivity.this, AddProducts.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void loginResult(String result) {
        JSONObject jsonObject;
        String result1 = "";

        try {
            jsonObject = new JSONObject(result);
            result1 = jsonObject.getString("user_type");

        } catch (JSONException e) {
            Log.d("error", e.getMessage());
        }

        Log.d("result", result);
        if (!result1.isEmpty() && result1.equalsIgnoreCase("admin")){
            progressDialog.dismiss();
            adminSession = new AdminSession(this);
            adminSession.saveUserSession(id);
            moveToAdmin();

        }else if (!result1.isEmpty() && result1.equalsIgnoreCase("normal")){
            progressDialog.dismiss();
            userSessionManager = new UserSessionManager(this);
            userSessionManager.saveUser(id);
            moveToHire();

        }else {
            progressDialog.dismiss();
            Objects.requireNonNull(password.getText()).clear();
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

    public void toastMessage(){
        if (message != null){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
}