package com.example.carhire.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

public class UserSessionManager {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String session = "user_session";
    String session_key = "id";

    public UserSessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(session, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void saveUser(EditText id){
        editor.putString(session_key, id.getText().toString().trim());
        editor.apply();
    }

    public String getUserData(){
        return sharedPreferences.getString(session_key, "");
    }

    public void removeSession(){
        editor.putString(session_key, "");
        editor.apply();
    }
}
