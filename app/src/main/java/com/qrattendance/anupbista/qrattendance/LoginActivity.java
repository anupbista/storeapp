package com.qrattendance.anupbista.qrattendance;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button loginBtn;
    private TextView registerLink;
    private String userName, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       username = findViewById(R.id.username);
       password = findViewById(R.id.password);
       loginBtn = findViewById(R.id.loginBtn);
       registerLink = findViewById(R.id.registerLink);

       registerLink.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
               LoginActivity.this.startActivity(registerIntent);
               finish();
           }
       });

       loginBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               login();
//               Intent intent = new Intent(LoginActivity.this, WifiConnectionReceiver.class);
//               intent.putExtra("SSID","Galacticos");
//               intent.putExtra("key","bernabeu@1902");
//               sendBroadcast(intent);
           }
       });
    }

    private void login() {
        init();
        if(!validateLogin()){
            Toast.makeText(this,"Login Failed", Toast.LENGTH_SHORT).show();
        }else{
            completeLogin();
        }
    }

    private void completeLogin() {
        JSONObject json = new JSONObject();
        try{
            json.put("userName",userName);
            json.put("userPassword",userPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL =  getResources().getString(R.string.logincustomer);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(LoginActivity.this,"Logging In", Toast.LENGTH_SHORT).show();
                try {
                    if(response.getBoolean("message")){
                        String userName = response.getJSONObject("details").getString("username");

                        SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(LoginActivity.this);
                        sharedPreferencesUser.setUsername(userName);

                        Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                        LoginActivity.this.startActivity(dashboardIntent);
                        finish();
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Login Failed. Incorrect username and password");
                        builder.setNegativeButton("Retry",null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this,"Error Connecting to API", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    private boolean validateLogin() {
        if(userName.isEmpty()){
            username.setError("Enter Username");
            return false;
        }
        if(userPassword.isEmpty()){
            password.setError("Enter Password");
            return false;
        }
        return true;
    }

    private void init() {
        userName = username.getText().toString().trim();
        userPassword = password.getText().toString().trim();
    }
}
