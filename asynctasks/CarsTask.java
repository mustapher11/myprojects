package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.ManageCarInterface;
import com.example.carhire.models.Car;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@SuppressLint("StaticFieldLeak")
public class CarsTask extends AsyncTask<Void, Void, ArrayList<Car>> {

    Context context;
    ManageCarInterface manageCarInterface;

    public CarsTask(Context context, ManageCarInterface manageCarInterface) {
        super();
        this.context = context;
        this.manageCarInterface = manageCarInterface;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(ArrayList<Car> cars) {
        manageCarInterface.fetchCarResult(cars);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected ArrayList<Car> doInBackground(Void... voids) {
        ArrayList<Car> cars = new ArrayList<>();
        byte[] bytes;
        Bitmap bitmap;

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/fetch_cars.php");
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
                bytes = Base64.decode(jsonObject.getString("image"), Base64.DEFAULT);
                InputStream inputStream1 = new ByteArrayInputStream(bytes);
                bitmap = BitmapFactory.decodeStream(inputStream1);

                cars.add(new Car(bitmap, jsonObject.getString("car_name"), jsonObject.getString("car_model"), jsonObject.getString("plate_number"), jsonObject.getString("cost") ));
            }
            Log.d("cars", "" + cars);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return cars;
    }
}