package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.asynctasks.UpdatePaymentTask;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.interfaces.ReservationInterface;

import java.util.Objects;

public class PaymentActivity extends AppCompatActivity implements ReservationInterface, NetworkConnectionInterface {

    ActionBar actionBar;
    Button pay;
    ProgressDialog progressDialog;
    String id = "";
    boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Payment Section");

        Intent intent = getIntent();
        id = intent.getStringExtra("id1");

        pay = findViewById(R.id.pay);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating payment...");
        progressDialog.setCanceledOnTouchOutside(false);

        pay.setOnClickListener(v -> confirmPayment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkManager networkManager = new NetworkManager(this, this);
        networkManager.execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("InflateParams")
    public void confirmPayment(){
        View view = LayoutInflater.from(this).inflate(R.layout.enter_payment_details, null);
        EditText reservationId = view.findViewById(R.id.reservation_id);
        EditText paymentCode = view.findViewById(R.id.payment_code);

        if (id != null){
            reservationId.setText(id);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Payment")
                .setView(view).setPositiveButton("Confirm", (dialog, which) -> {
                    if (result){
                        if (reservationId.getText().toString().trim().replace(" ", "").isEmpty() && paymentCode.getText().toString().trim().replace(" ", "").isEmpty()){
                            Toast.makeText(this, "ID field and Payment Code field cannot be empty!", Toast.LENGTH_LONG).show();

                        }else if (reservationId.getText().toString().trim().replace(" ", "").isEmpty()){
                            Toast.makeText(this, "ID field cannot be empty!", Toast.LENGTH_LONG).show();

                        }else if (paymentCode.getText().toString().trim().replace(" ", "").isEmpty()){
                            Toast.makeText(this, "Payment Code field cannot be empty!", Toast.LENGTH_LONG).show();

                        }else if (reservationId.getText().toString().trim().replace(" ", "").length() > 11){
                            Toast.makeText(this, "Reservation ID should be less than 10 characters!", Toast.LENGTH_LONG).show();

                        }else if (paymentCode.getText().toString().trim().replace(" ", "").length() > 10){
                            Toast.makeText(this, "Reservation ID should be less than 10 characters!", Toast.LENGTH_LONG).show();

                        }else {
                            progressDialog.show();
                            UpdatePaymentTask updatePaymentTask = new UpdatePaymentTask(this, this);
                            updatePaymentTask.execute(paymentCode.getText().toString().trim().replace(" ", ""), reservationId.getText().toString().substring(1).trim());
                        }

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Please ensure that you have an active internet connection!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void pingResult(boolean result) {
        this.result = result;
    }

    @Override
    public void updatePaymentResult(String response) {
        if (response != null){
            progressDialog.dismiss();
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
    }
}