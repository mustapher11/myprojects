package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.ReservationInterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AddReservationTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    Context context;
    ReservationInterface reservationInterface;

    public AddReservationTask(Context context, ReservationInterface reservationInterface) {
        super();
        this.context = context;
        this.reservationInterface = reservationInterface;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String s) {
        reservationInterface.getReservationResult(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {

        String response = null;

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/add_reservations.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String userId = URLEncoder.encode("user_id", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));
            String carImage = URLEncoder.encode("car_image", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[1], String.valueOf(StandardCharsets.UTF_8));
            String carName = URLEncoder.encode("car_name", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[2], String.valueOf(StandardCharsets.UTF_8));
            String carModel = URLEncoder.encode("car_model", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[3], String.valueOf(StandardCharsets.UTF_8));
            String carNumber = URLEncoder.encode("plate_number", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[4], String.valueOf(StandardCharsets.UTF_8));
            String carCost = URLEncoder.encode("cost", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[5], String.valueOf(StandardCharsets.UTF_8));
            String assignDriver = URLEncoder.encode("assign_driver", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[6], String.valueOf(StandardCharsets.UTF_8));
            String paymentCode = URLEncoder.encode("payment_code", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode("Payment pending", String.valueOf(StandardCharsets.UTF_8));
            String status = URLEncoder.encode("status", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode("Pending", String.valueOf(StandardCharsets.UTF_8));
            String assigned_driver = URLEncoder.encode("assigned_driver", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode("None", String.valueOf(StandardCharsets.UTF_8));

            String data = userId + "&" + carImage + "&" + carName + "&" + carModel + "&" + carNumber + "&" + carCost + "&" + assignDriver + "&" + paymentCode + "&" +  status + "&" + assigned_driver;

            bufferedWriter.write(data);
            Log.d("data", data);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            response = bufferedReader.readLine();

            httpURLConnection.disconnect();

        } catch (IOException e) {
            Log.d("Error", e.getMessage());
        }

        return response;
    }
}