package com.anupbista.store;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetail extends AppCompatActivity {

    String productID;
    private TextView productName;
    private TextView productDesc;
    private TextView productCat;
    private TextView productSize;
    private TextView productColor;
    private TextView productBrand;
    private TextView productPrice;
    private ImageView productimage;
    Bitmap productImage;
    Bitmap productImages;

    ConstraintLayout prodDetailContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        getSupportActionBar().setTitle("Checkout Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productName = findViewById(R.id.name);
        productDesc = findViewById(R.id.desc);
        productCat = findViewById(R.id.category);
        productSize = findViewById(R.id.size);
        productColor = findViewById(R.id.color);
        productBrand = findViewById(R.id.brand);
        productPrice = findViewById(R.id.price);
        productimage = findViewById(R.id.productImage);
        prodDetailContainer = findViewById(R.id.productDetailContainer);
        getIncomingIntent();
    }
    private void getIncomingIntent(){
        if (getIntent().hasExtra("productID")){
            productID = getIntent().getStringExtra("productID");
            getProductInfo();
        }
    }

    private void getProductInfo() {
        final ProgressDialog progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Loading Product Details...");
        progressDialog.show();
        JSONObject json = new JSONObject();
        try{
            json.put("productID",productID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL =  getResources().getString(R.string.getProductInfo);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();

                try {
                    if(response.getBoolean("message")){

                        System.out.println(response);
                        JSONArray productArray = response.getJSONArray("productInfo");
                        for (int i=0;i<productArray.length();i++){
                            final JSONObject productObject = productArray.getJSONObject(i);
                            JSONObject json = new JSONObject();
                            try{
                                json.put("productID",productObject.getString("productID"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String URL =  getResources().getString(R.string.getProductImage);
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getBoolean("message")){
                                            prodDetailContainer.setVisibility(View.VISIBLE);
                                            byte[] encodeByte = Base64.decode(response.getString("details"),Base64.DEFAULT);
                                            Bitmap productImage = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                                            productImages = productImage;

                                            productName.setText(productObject.getString("productname"));
                                            productDesc.setText(productObject.getString("productdesc"));
                                            productCat.setText(productObject.getString("productcategory"));
                                            productSize.setText(productObject.getString("productsize"));
                                            productColor.setText(productObject.getString("productcolor"));
                                            productBrand.setText(productObject.getString("productbrand"));
                                            productPrice.setText(productObject.getString("productprice"));
                                            productimage.setImageBitmap(productImages);

                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"Failed loading Image", Toast.LENGTH_SHORT).show();
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
                    else{
                        Toast.makeText(getApplicationContext(),"Cannot get Product Information", Toast.LENGTH_SHORT).show();
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
