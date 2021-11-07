package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.carhire.interfaces.UserProfileInterface;
import com.example.carhire.models.User;

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

public class FetchCustomerTask extends AsyncTask<Void, Void, ArrayList<User>> {

    @SuppressLint("StaticFieldLeak")
    Context context;
    UserProfileInterface userProfileInterface;

    public FetchCustomerTask(Context context, UserProfileInterface userProfileInterface) {
        super();
        this.context = context;
        this.userProfileInterface = userProfileInterface;
    }

    @Override
    protected ArrayList<User> doInBackground(Void... voids) {
        ArrayList<User> users = new ArrayList<>();

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/fetch_users.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String jsonResponse = bufferedReader.readLine();

            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                users.add(new User("First Name: " + jsonObject.getString("first_name"), "Last Name: " + jsonObject.getString("last_name"), "ID Number: " + jsonObject.getString("id_number"), "Phone Number: " + jsonObject.getString("phone_number")));
            }

            httpURLConnection.disconnect();

        } catch (IOException | JSONException e) {
            Log.d("Error", e.getMessage());
        }

        return users;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(ArrayList<User> users) {
        userProfileInterface.fetchCustomerResult(users);
    }
}