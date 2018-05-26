package com.qrattendance.anupbista.qrattendance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(MainActivity.this);
                if(sharedPreferencesUser.getUsername()!=""){
                    Intent dashboardIntent = new Intent(MainActivity.this, DashboardActivity.class);
                    MainActivity.this.startActivity(dashboardIntent);
                    finish();
                }else{
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(loginIntent);
                    finish();
                }
            }
        },SPLASH_TIME_OUT);
    }

}