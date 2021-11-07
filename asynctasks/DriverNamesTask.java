package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.carhire.interfaces.ManageDriverInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@SuppressLint("StaticFieldLeak")
public class DriverNamesTask extends AsyncTask<Void, Void, ArrayList<String>> {

    Context context;
    ManageDriverInterface manageDriverInterface;

    public DriverNamesTask(Context context, ManageDriverInterface manageDriverInterface) {
        super();
        this.context = context;
        this.manageDriverInterface = manageDriverInterface;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(ArrayList<String> driver1s) {
        manageDriverInterface.driverNamesResult(driver1s);
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        ArrayList<String> drivers = new ArrayList<>();

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/fetch_drivers.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String result = bufferedReader.readLine();
            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                drivers.add(jsonObject.getString("first_name")+ " " + jsonObject.getString("last_name"));
            }

            httpURLConnection.disconnect();

        } catch (IOException | JSONException e) {
            Log.d("Error", e.getMessage());
        }
        return drivers;
    }
}