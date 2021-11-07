package com.example.carhire.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

public class AdminSession {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String session = "admin_session";
    String session_key = "id";

    public AdminSession(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(session, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void saveUserSession(EditText id){
        editor.putString(session_key, id.getText().toString().trim());
        editor.apply();
    }

    public String getAdminData(){
        return sharedPreferences.getString(session_key, "");
    }

    public void removeSession(){
        editor.putString(session_key, "");
        editor.apply();
    }
}
