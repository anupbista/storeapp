package com.qrattendance.anupbista.qrattendance;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUser {
    Context context;
    private String username;
    SharedPreferences sharedPreferences;

    public SharedPreferencesUser(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userinfo",Context.MODE_PRIVATE);
    }

    public String getUsername() {
        username= sharedPreferences.getString("userdata","");
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userdata",username).commit();
    }

    public void remove(){
        sharedPreferences.edit().clear().commit();
    }
}
