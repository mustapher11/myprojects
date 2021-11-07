package com.example.carhire.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.carhire.interfaces.NetworkConnectionInterface;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkManager extends AsyncTask<Void, Void, Boolean> {

    @SuppressLint("StaticFieldLeak")
    Context context;
    NetworkConnectionInterface networkConnectionInterface;

    public NetworkManager(Context context, NetworkConnectionInterface networkConnectionInterface) {
        super();
        this.context = context;
        this.networkConnectionInterface = networkConnectionInterface;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        boolean result = false;

        try {
            URL url = new URL("https://www.google.com/");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setConnectTimeout(5000);
            httpsURLConnection.connect();

            int responseCode = httpsURLConnection.getResponseCode();

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected() && responseCode == HttpsURLConnection.HTTP_OK){
                result = true;
            }

        } catch (IOException e) {
            Log.d("error", e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean b) {
        networkConnectionInterface.pingResult(b);
    }
}