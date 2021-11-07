package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.ReservationInterface;

import org.json.JSONArray;
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

@SuppressLint("StaticFieldLeak")
public class FetchAdminReservationDetailsTask extends AsyncTask<String, Void, JSONObject> {

    Context context;
    ReservationInterface reservationInterface;

    public FetchAdminReservationDetailsTask(Context context, ReservationInterface reservationInterface) {
        super();
        this.context = context;
        this.reservationInterface = reservationInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        reservationInterface.reservationDetails1Result(jsonObject);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected JSONObject doInBackground(String... strings) {
        JSONObject jsonObject = null;

        try {
            URL url = new URL("https://trainingmia.000webhostapp.com/fetch_reservationDetails1.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String data = URLEncoder.encode("user_id", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[1], String.valueOf(StandardCharsets.UTF_8));
            String id = URLEncoder.encode("id", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));

            String data1 = data + "&" + id;

            bufferedWriter.write(data1);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String jsonResponse = bufferedReader.readLine();

            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
            }

            assert jsonObject != null;
            Log.d("jsonObject", jsonObject.toString());

            httpURLConnection.disconnect();

        } catch (IOException | JSONException e) {
            Log.d("error", e.getMessage());
        }

        return jsonObject;
    }
}
