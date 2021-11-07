package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carhire.asynctasks.FetchReservationDetailsTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.interfaces.ReservationInterface;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

public class HistoryDetailsActivity extends AppCompatActivity implements NetworkConnectionInterface, ReservationInterface {

    ActionBar actionBar;
    ConstraintLayout constraintLayout;
    ImageView car_image;
    TextView id, car_name, car_model, cost, plate_number, payment_code, status;
    Button mtp;
    ProgressDialog progressDialog;
    Snackbar snackbar;

    String reservationId = "";
    FetchReservationDetailsTask fetchReservationDetailsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Hiring Details");

        Intent intent = getIntent();
        reservationId = intent.getStringExtra("id");

        constraintLayout = findViewById(R.id.constraint_layout);
        car_image = findViewById(R.id.car_image);
        id = findViewById(R.id.id);
        car_name = findViewById(R.id.car_name);
        car_model = findViewById(R.id.car_model);
        cost = findViewById(R.id.cost);
        plate_number = findViewById(R.id.plate_number);
        payment_code = findViewById(R.id.payment_code);
        status = findViewById(R.id.status);
        mtp = findViewById(R.id.mtp);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching reservation details...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Events Handling.
        mtp.setOnClickListener(v -> moveToPayment());
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

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void pingResult(boolean result) {
        if (result){
            fetchReservationDetailsTask = new FetchReservationDetailsTask(this, this);
            fetchReservationDetailsTask.execute(reservationId);

        }else {
            progressDialog.dismiss();
            showSnackBar();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void reservationDetailsResult(JSONObject jsonObject) {

        if (jsonObject != null){

            byte[] bytes;
            Bitmap bitmap;
            try {
                String idText = jsonObject.getString("id");
                String carName = jsonObject.getString("car_name");
                String carModel = jsonObject.getString("car_model");
                String costText = jsonObject.getString("cost");
                String paymentCode = jsonObject.getString("payment_code");
                String plateNumber = jsonObject.getString("plate_number");
                String statusText = jsonObject.getString("status");

                bytes = Base64.decode(jsonObject.getString("car_image"), Base64.DEFAULT);
                InputStream inputStream1 = new ByteArrayInputStream(bytes);
                bitmap = BitmapFactory.decodeStream(inputStream1);

                progressDialog.dismiss();
                car_image.setImageBitmap(bitmap);
                id.setText("#" + idText);
                car_name.setText(carName);
                car_model.setText(carModel);
                cost.setText(costText);
                plate_number.setText(plateNumber);
                payment_code.setText(paymentCode);
                status.setText(statusText);

            } catch (JSONException e) {
                Log.d("error", e.getMessage());
            }

            checkPayment();
            checkStatus();

        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "No results!", Toast.LENGTH_SHORT).show();
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
                    new NetworkManager(HistoryDetailsActivity.this, HistoryDetailsActivity.this).execute();
                }
        );
        snackbar.show();
    }

    public void moveToPayment(){
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("id1", id.getText().toString());
        startActivity(intent);
    }

    public void checkStatus(){
        String statusType = status.getText().toString();

        if (statusType.equalsIgnoreCase("approved")){
            status.setBackgroundColor(Color.GREEN);

        }else if (statusType.equalsIgnoreCase("pending")){
            status.setBackgroundColor(Color.parseColor("#FFBB86FC"));

        }else {
            status.setBackgroundColor(Color.RED);
        }
    }

    public void checkPayment(){
        if (!payment_code.getText().toString().equalsIgnoreCase("Payment pending")){
            mtp.setEnabled(false);
        }
    }
}