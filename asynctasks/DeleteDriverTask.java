package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

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
public class DeleteDriverTask extends AsyncTask<String, Void, String> {

    Context context;
    ManageDriverInterface manageDriverInterface;

    public DeleteDriverTask(Context context, ManageDriverInterface manageDriverInterface) {
        super();
        this.context = context;
        this.manageDriverInterface = manageDriverInterface;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String s) {
        manageDriverInterface.deleteDriverResult(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {

        String response = null;

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/delete.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String data = URLEncoder.encode("driver_id", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));

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