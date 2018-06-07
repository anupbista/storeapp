package com.anupbista.store;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private  RecyclerView.Adapter adapter;
    private List<CheckoutItemsBill> checkoutItemsBills;
    double billTotal=0;
    private TextView billTotalPrice;
    private TextView checkoutBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        getSupportActionBar().setTitle("Checkout Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.checkDetailsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkoutItemsBills = new ArrayList<>();

        billTotalPrice = findViewById(R.id.billTotal);
        checkoutBy = findViewById(R.id.billCheckoutBy);

        getCheckoutDetailsBill();
    }

    private void getCheckoutDetailsBill() {
        final ProgressDialog progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Loading Bill Data...");
        progressDialog.show();

        SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(this);
        final String userName = sharedPreferencesUser.getUsername();
        JSONObject json = new JSONObject();
        try{
            json.put("userName",userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        billTotal = 0;
        String URL =  getResources().getString(R.string.getCheckoutBill);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    if(response.getBoolean("message")){
                        JSONArray billListtArray = response.getJSONArray("billData");
                        System.out.println(billListtArray);
                        for (int i = 0; i < billListtArray.length() ; i++) {
                            JSONObject jsonObject  =billListtArray.getJSONObject(i);
                            CheckoutItemsBill checkoutItemsBill = new CheckoutItemsBill(jsonObject.getString("productID"),jsonObject.getString("productName"),jsonObject.getString("productQuantity"),jsonObject.getString("productPrice"));
                            checkoutItemsBills.add(checkoutItemsBill);
                            billTotal += Double.parseDouble(jsonObject.getString("productPrice"));
                            checkoutBy.setText("Checkout By: "+jsonObject.getString("checkoutBy"));
                        }

                        adapter = new CheckoutItemsAdapter(checkoutItemsBills,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        billTotalPrice.setText("Total: Rs. "+String.valueOf(billTotal));
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error Fetching Data", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error Connecting to API", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}
