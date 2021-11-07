package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.ReservationInterface;
import com.example.carhire.models.HiringHistory;

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
import java.util.ArrayList;

public class FetchIdTask extends AsyncTask<String, Void, ArrayList<HiringHistory>> {

    @SuppressLint("StaticFieldLeak")
    Context context;
    ReservationInterface reservationInterface;

    public FetchIdTask(Context context, ReservationInterface reservationInterface) {
        super();
        this.context = context;
        this.reservationInterface = reservationInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<HiringHistory> hiringHistoryArrayList) {
        reservationInterface.fetchReservationHistoryResult(hiringHistoryArrayList);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected ArrayList<HiringHistory> doInBackground(String... strings) {

        ArrayList<HiringHistory> hiringHistoryArrayList = new ArrayList<>();

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/fetch_ids.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String data = URLEncoder.encode("user_id", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String jsonResponse = bufferedReader.readLine();

            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                hiringHistoryArrayList.add(new HiringHistory(jsonObject.getString("id"), jsonObject.getString("status"), jsonObject.getString("date")));
            }

            httpURLConnection.disconnect();

        } catch (IOException | JSONException e) {
            Log.d("error", e.getMessage());
        }

        return hiringHistoryArrayList;
    }
}
