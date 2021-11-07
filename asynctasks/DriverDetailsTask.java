package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.ManageDriverInterface;

import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.List;

public class DriverDetailsTask extends AsyncTask<String, Void, List<String>> {

    @SuppressLint("StaticFieldLeak")
    Context context;
    ManageDriverInterface manageDriverInterface;
    ProgressDialog progressDialog;

    public DriverDetailsTask(Context context, ManageDriverInterface manageDriverInterface, ProgressDialog progressDialog) {
        super();
        this.context = context;
        this.manageDriverInterface = manageDriverInterface;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Fetching driver details...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(List<String> stringList) {
        manageDriverInterface.searchDriverResult(stringList);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> driverDetails = new ArrayList<>();

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/search_driver.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String driverId = URLEncoder.encode("driver_id", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));
            String driverPhone = URLEncoder.encode("driver_phone", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[1], String.valueOf(StandardCharsets.UTF_8));

            String data = driverId + "&" + driverPhone;

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String jsonResult = bufferedReader.readLine();
            JSONObject jsonObject = new JSONObject(jsonResult);

            driverDetails.add(jsonObject.getString("first_name"));
            driverDetails.add(jsonObject.getString("last_name"));
            driverDetails.add(jsonObject.getString("driver_id"));
            driverDetails.add(jsonObject.getString("driver_phone"));

            httpURLConnection.disconnect();

        } catch (IOException | JSONException e) {
            Log.d("error", e.getMessage());
        }

        return driverDetails;
    }
}