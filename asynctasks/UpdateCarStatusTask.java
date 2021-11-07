package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.ManageCarInterface;

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

public class UpdateCarStatusTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    Context context;
    ManageCarInterface manageCarInterface;

    public UpdateCarStatusTask(Context context, ManageCarInterface manageCarInterface) {
        super();
        this.context = context;
        this.manageCarInterface = manageCarInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        manageCarInterface.updateCarStatusResult(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {
        String response = "";

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/updateCarStatus.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String status = URLEncoder.encode("status", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[0], String.valueOf(StandardCharsets.UTF_8));
            String plateNumber = URLEncoder.encode("plate_number", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(strings[1], String.valueOf(StandardCharsets.UTF_8));

            String data = status + "&" + plateNumber;

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            response = bufferedReader.readLine();

            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
