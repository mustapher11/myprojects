package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.example.carhire.interfaces.RegisterInterface;

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
public class RegisterTask extends AsyncTask<String, Void, String> {

    Context context;
    ProgressDialog progressDialog;
    EditText firstName, lastName, id_number, phone, password;
    String userType = "normal";

    RegisterInterface registerInterface;

    public RegisterTask(Context context, RegisterInterface registerInterface, ProgressDialog progressDialog, EditText firstName, EditText lastName, EditText id_number, EditText phone, EditText password) {
        super();
        this.context = context;
        this.progressDialog = progressDialog;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id_number = id_number;
        this.phone = phone;
        this.password = password;
        this.registerInterface = registerInterface;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("Register");
        progressDialog.setMessage("Registering...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        registerInterface.registerResult(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {

        String result = "";

        try {
            URL url = new URL("http://trainingmia.000webhostapp.com/register.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            String firstName_data = URLEncoder.encode("first_name", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(firstName.getText().toString().trim(), String.valueOf(StandardCharsets.UTF_8));
            String lastName_data = URLEncoder.encode("last_name", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(lastName.getText().toString().trim(), String.valueOf(StandardCharsets.UTF_8));
            String idNumber_data = URLEncoder.encode("id_number", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(id_number.getText().toString().trim(), String.valueOf(StandardCharsets.UTF_8));
            String phone_data = URLEncoder.encode("phone_number", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(phone.getText().toString().trim(), String.valueOf(StandardCharsets.UTF_8));
            String password_data = URLEncoder.encode("password", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(password.getText().toString().trim(), String.valueOf(StandardCharsets.UTF_8));
            String userType_data = URLEncoder.encode("user_type", String.valueOf(StandardCharsets.UTF_8)) + "=" + URLEncoder.encode(userType.trim(), String.valueOf(StandardCharsets.UTF_8));

            String data = firstName_data + "&" + lastName_data + "&" + idNumber_data + "&" + phone_data + "&" + password_data + "&" + userType_data;

            bufferedWriter.write(data);
            bufferedWriter.close();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            result = bufferedReader.readLine();
            Log.d("result", result);

            httpURLConnection.disconnect();

        } catch (IOException e) {
            Log.d("error", e.getMessage());
        }

        return result;
    }
}