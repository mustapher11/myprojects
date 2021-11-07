package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.ManageCarInterface;

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


public class ManageCarTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    Context context;
    ManageCarInterface manageCarInterface;
    ProgressDialog progressDialog;

    public ManageCarTask(Context context, ManageCarInterface manageCarInterface, ProgressDialog progressDialog) {
        super();
        this.context = context;
        this.manageCarInterface = manageCarInterface;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Saving car details...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        manageCarInterface.addCarResult(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {

        String response = null;

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/add_car.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String image = URLEncoder.encode("image", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));
            String carName = URLEncoder.encode("car_name", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[1], String.valueOf(StandardCharsets.UTF_8));
            String carModel = URLEncoder.encode("car_model", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[2], String.valueOf(StandardCharsets.UTF_8));
            String carNumber = URLEncoder.encode("plate_number", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[3], String.valueOf(StandardCharsets.UTF_8));
            String seats = URLEncoder.encode("seats", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[4], String.valueOf(StandardCharsets.UTF_8));
            String cost = URLEncoder.encode("cost", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[5], String.valueOf(StandardCharsets.UTF_8));
            String adminId = URLEncoder.encode("admin_id", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[6], String.valueOf(StandardCharsets.UTF_8));
            String status = URLEncoder.encode("status", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode("available", String.valueOf(StandardCharsets.UTF_8));

            String data = image + "&" + carName + "&" + carModel + "&" + carNumber + "&" + seats + "&" + cost + "&" + adminId + "&" + status;

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            response = bufferedReader.readLine();

            httpURLConnection.disconnect();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}