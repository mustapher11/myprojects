package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.LogInInterface;

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
public class LoginTask extends AsyncTask<Void, Void, String> {

    Context context;
    ProgressDialog progressDialog;
    EditText id_number, password;

    LogInInterface logInInterface;

    public LoginTask(Context context, LogInInterface logInInterface, EditText id_number, EditText password, ProgressDialog progressDialog) {
        super();
        this.context = context;
        this.id_number = id_number;
        this.password = password;
        this.progressDialog = progressDialog;
        this.logInInterface = logInInterface;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null){
            Log.d("result", s);
            logInInterface.loginResult(s);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(Void... voids) {
        String jsonResult = null;

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/login.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String idNumber_data = URLEncoder.encode("id_number", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(id_number.getText().toString().trim(), String.valueOf(StandardCharsets.UTF_8));
            String password_data = URLEncoder.encode("password", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(password.getText().toString().trim(), String.valueOf(StandardCharsets.UTF_8));
            String data = idNumber_data + "&" + password_data;

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            jsonResult = bufferedReader.readLine();
            Log.d("jsonResult", jsonResult);

        } catch (IOException e) {
            Log.d("error", e.getMessage());
        }

        return jsonResult;
    }
}