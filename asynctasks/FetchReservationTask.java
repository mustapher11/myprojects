package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.ReservationInterface;
import com.example.carhire.models.Reservation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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

@SuppressLint("StaticFieldLeak")
public class FetchReservationTask extends AsyncTask<String, Void, ArrayList<Reservation>> {

    Context context;
    ReservationInterface reservationInterface;

    public FetchReservationTask(Context context, ReservationInterface reservationInterface) {
        super();
        this.context = context;
        this.reservationInterface = reservationInterface;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(ArrayList<Reservation> reservations) {
        reservationInterface.fetchReservationResult(reservations);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected ArrayList<Reservation> doInBackground(String... strings) {
        ArrayList<Reservation> reservations = new ArrayList<>();
        byte[] bytes;
        Bitmap bitmap;

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/fetch_reservations.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String data = URLEncoder.encode("status", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String jsonResponse = bufferedReader.readLine();

            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                bytes = Base64.decode(jsonObject.getString("car_image"), Base64.DEFAULT);
                InputStream inputStream1 = new ByteArrayInputStream(bytes);
                bitmap = BitmapFactory.decodeStream(inputStream1);

                reservations.add(new Reservation(jsonObject.getString("id"), bitmap, jsonObject.getString("user_id"), jsonObject.getString("cost"), jsonObject.getString("payment_code"), jsonObject.getString("date")));
            }

            httpURLConnection.disconnect();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return reservations;
    }
}