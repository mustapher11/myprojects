package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.ManageDriverInterface;

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

@SuppressLint("StaticFieldLeak")
public class AddDriverTask extends AsyncTask<String, Void, String> {

    Context context;
    ProgressDialog progressDialog;
    ManageDriverInterface manageDriverInterface;

    public AddDriverTask(Context context, ManageDriverInterface manageDriverInterface, ProgressDialog progressDialog) {
        super();
        this.context = context;
        this.manageDriverInterface = manageDriverInterface;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Saving driver details");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        manageDriverInterface.addDriverResult(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {

        String response = "";

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/add_driver.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String driverFirstName = URLEncoder.encode("first_name", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));
            String driverLastName = URLEncoder.encode("last_name", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[1], String.valueOf(StandardCharsets.UTF_8));
            String driverId = URLEncoder.encode("driver_id", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[2], String.valueOf(StandardCharsets.UTF_8));
            String driverPhone = URLEncoder.encode("driver_phone", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[3], String.valueOf(StandardCharsets.UTF_8));
            String adminId = URLEncoder.encode("admin_id", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[4], String.valueOf(StandardCharsets.UTF_8));

            String data = driverFirstName + "&" + driverLastName + "&" + driverId + "&" + driverPhone + "&" + adminId;

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
