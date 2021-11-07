package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.UserProfileInterface;

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
public class UpdateProfileTask extends AsyncTask<String, Void, String> {

    Context context;
    UserProfileInterface userProfileInterface;

    public UpdateProfileTask(Context context, UserProfileInterface userProfileInterface) {
        super();
        this.context = context;
        this.userProfileInterface = userProfileInterface;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String s) {
        userProfileInterface.updateProfileResult(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {
        String response = null;

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/update_profile.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String firstName = URLEncoder.encode("first_name", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));
            String lastName = URLEncoder.encode("last_name", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[1], String.valueOf(StandardCharsets.UTF_8));
            String phoneNumber = URLEncoder.encode("phone_number", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[2], String.valueOf(StandardCharsets.UTF_8));
            String password = URLEncoder.encode("password", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[3], String.valueOf(StandardCharsets.UTF_8));
            String idNumber = URLEncoder.encode("id_number", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[4], String.valueOf(StandardCharsets.UTF_8));
            String password2 = URLEncoder.encode("password1", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[5], String.valueOf(StandardCharsets.UTF_8));

            String data = firstName + "&" + lastName + "&" + phoneNumber + "&" + password + "&" + idNumber + "&" + password2;

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            response = bufferedReader.readLine();
            Log.d("response", response);

            httpURLConnection.disconnect();

        } catch (IOException e) {
            Log.d("error", e.getMessage());
        }

        return response;
    }
}
