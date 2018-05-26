package com.qrattendance.anupbista.qrattendance;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText first_name, last_name, username, email, address, phonenumber, password, rePassword;
    private String firstName, lastName, userName, userEmail, userAddress, phoneNumber, userPassword, userrePassword, usergender;
    private RadioButton userGender;
    private RadioGroup gender;
    private Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        phonenumber = findViewById(R.id.phonenumber);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        signUpBtn = findViewById(R.id.signUpBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();

            }
        });
    }

    private void register() {
        init();
        if (!validateLogin()){
            Toast.makeText(this,"SignUp Failed", Toast.LENGTH_SHORT).show();
        }else{
            completeRegister();
        }
    }

    private void completeRegister() {
        final JSONObject json = new JSONObject();
        try{
            json.put("firstName",firstName);
            json.put("lastName",lastName);
            json.put("userName",userName);
            json.put("userEmail",userEmail);
            json.put("userAddress",userAddress);
            json.put("phoneNumber",phoneNumber);
            json.put("userGender",usergender);
            json.put("userPassword",userPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL =  getResources().getString(R.string.registercustomer);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(RegisterActivity.this,"Registering", Toast.LENGTH_SHORT).show();
                try {
                    if(response.getBoolean("valid")){

                        SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(RegisterActivity.this);
                        sharedPreferencesUser.setUsername(userName);

                        Intent dashboardIntent = new Intent(RegisterActivity.this, DashboardActivity.class);
                        RegisterActivity.this.startActivity(dashboardIntent);
                        finish();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this,"Error Connecting to API", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        RegisterActivity.this.startActivity(registerIntent);
        finish();
    }

    private Boolean validateLogin(){
        if(firstName.isEmpty()){
            first_name.setError("Enter First Name");
            return false;
        }
        if(lastName.isEmpty()){
            last_name.setError("Enter Last Name");
            return false;
        }
        if(userName.isEmpty()){
            username.setError("Enter SharedPreferencesUser Name");
            return false;
        }
        if(userEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            email.setError("Enter valid Email Address");
            return false;
        }
        if(userAddress.isEmpty()){
            email.setError("Enter Address");
            return false;
        }
        if(phoneNumber.isEmpty()){
            phonenumber.setError("Enter Phone Number");
            return false;
        }
        if(userPassword.isEmpty()){
            password.setError("Enter Password");
            return false;
        }
        if(userrePassword.isEmpty()){
            rePassword.setError("Enter Re Password");
            return false;
        }
        if(!userPassword.equals(userrePassword)){
            rePassword.setError("Password do not match");
            return false;
        }

        return true;
    }

    public void onGenderButtonClicked(View view){
        gender = findViewById(R.id.gender);
        int genderId = gender.getCheckedRadioButtonId();
        userGender = findViewById(genderId);
        usergender = (String) userGender.getText();
    }

    private void init() {
        firstName = first_name.getText().toString().trim();
        lastName = last_name.getText().toString().trim();
        userName = username.getText().toString().trim();
        userEmail = email.getText().toString().trim();
        userAddress = address.getText().toString().trim();
        phoneNumber = phonenumber.getText().toString().trim();
        userPassword = password.getText().toString().trim();
        userrePassword = rePassword.getText().toString().trim();

        gender = findViewById(R.id.gender);
        int genderId = gender.getCheckedRadioButtonId();
        userGender = findViewById(genderId);
        usergender = (String) userGender.getText();
    }
}
