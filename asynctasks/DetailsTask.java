package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.UserProfileInterface;

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

public class DetailsTask extends AsyncTask<String, Void, List<String>> {

    @SuppressLint("StaticFieldLeak")
    Context context;

    UserProfileInterface profileInterface;

    public DetailsTask(Context context, UserProfileInterface profileInterface) {
        super();
        this.context = context;
        this.profileInterface = profileInterface;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        profileInterface.getUserData(strings);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected List<String> doInBackground(String... strings) {

        List<String> userDetails = new ArrayList<>();

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/user_details.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String idNumber = URLEncoder.encode("id_number", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));

            bufferedWriter.write(idNumber);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String jsonResult = bufferedReader.readLine();
            Log.d("jsonResult", jsonResult);

            JSONObject jsonObject = new JSONObject(jsonResult);

            userDetails.add(jsonObject.getString("first_name"));
            userDetails.add(jsonObject.getString("last_name"));
            userDetails.add(jsonObject.getString("phone_number"));

            httpURLConnection.disconnect();

        } catch (IOException | JSONException e) {
            Log.d("error", e.getMessage());
        }

        return userDetails;
    }
}