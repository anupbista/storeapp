package com.anupbista.store;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScanFragment extends Fragment {

    private TextView productID, productName, productCat, productSize, productColor, productBrand, productPrice, productDesc, scanMessage;
    private CardView productInfoCard;
    private Button addToCartBtn;
    private ImageView productImage;
    private EditText productQuantity;
    JSONObject productInformation;
    //qr code scanner object
    private IntentIntegrator qrScan;
    int maxQuantity;
    int setHomeDelivery = 0;

//    for recommendation of product based on scanned product
    RecyclerView recyclerView;
    ScanRecommendationAdapter adapter;
    List<Products> productsList;
    Bitmap rproductImage;
    Bitmap rproductImages;
    CardView scanRecCardView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View dashboardView =  inflater.inflate(R.layout.b_nav_scan,container,false);

        productID = dashboardView.findViewById(R.id.productID);
        productName = dashboardView.findViewById(R.id.productName);
        productCat= dashboardView.findViewById(R.id.productCat);
        productSize = dashboardView.findViewById(R.id.productSize);
        productColor = dashboardView.findViewById(R.id.productColor);
        productBrand = dashboardView.findViewById(R.id.productBrand);
        productPrice = dashboardView.findViewById(R.id.productPrice);
        productDesc = dashboardView.findViewById(R.id.productDesc);
        productInfoCard = dashboardView.findViewById(R.id.productInfoCard);
        scanMessage = dashboardView.findViewById(R.id.scanMessage);
        addToCartBtn = dashboardView.findViewById(R.id.addToCartBtn);
        productQuantity = dashboardView.findViewById(R.id.productQuantity);
        productImage = dashboardView.findViewById(R.id.productImage);
        CheckBox checkBox = dashboardView.findViewById(R.id.itemHomeDelivery);
        scanRecCardView = dashboardView.findViewById(R.id.scanRecCardView);

        productsList = new ArrayList<>();
        recyclerView = dashboardView.findViewById(R.id.recommendationScanRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayout.HORIZONTAL, false));

        adapter = new ScanRecommendationAdapter(productsList,getContext());
        recyclerView.setAdapter(adapter);


        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()){
                    setHomeDelivery  = 1;
                }
                else{
                    setHomeDelivery = 0;
                }
            }
        });
        maxQuantity =1;

        productQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                JSONObject json = new JSONObject();
                try{
                    json.put("productID",productInformation.getString("productID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String URL =  getResources().getString(R.string.getProductQuantity);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("message")){
                                maxQuantity = Integer.parseInt(response.getJSONObject("productQuantity").getString("productquantity"));
                            }
                            else{
                                Toast.makeText(getActivity(),"Failed receiving correct product quantity", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Error Connecting to API", Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
            }
        });

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(v.getContext());
                JSONObject json = new JSONObject();
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                String time = sdf.format(d);
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh");
                String onlyTime = timeFormat.format(new Date());
                String currentTimeDate = DateFormat.getDateTimeInstance().format(new Date());
                Bitmap pImage = ((BitmapDrawable)productImage.getDrawable()).getBitmap();
                int pq;
                if (!productQuantity.getText().toString().isEmpty()){
                    if (Integer.parseInt(productQuantity.getText().toString()) <= maxQuantity){
                        pq = Integer.parseInt(productQuantity.getText().toString());
                        try{
                            json.put("userName",sharedPreferencesUser.getUsername());
                            json.put("productOnCartID",sharedPreferencesUser.getUsername()+productInformation.getString("productID")+time);
                            json.put("productName",productInformation.getString("productName"));
                            json.put("productID",productInformation.getString("productID"));
                            json.put("productCat",productInformation.getString("productCat"));
                            json.put("productSize",productInformation.getString("productSize"));
                            json.put("productBrand",productInformation.getString("productBrand"));
                            json.put("productColor",productInformation.getString("productColor"));
                            json.put("productPrice",String.valueOf(pq*Integer.parseInt(productInformation.getString("productPrice"))));
                            json.put("productDesc",productInformation.getString("productDesc"));
                            json.put("productQuantity",pq);
                            json.put("productAddedDateTime",currentTimeDate);
                            json.put("productImage",pImage);
                            json.put("time",onlyTime);
                            json.put("homedelivery",setHomeDelivery);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String URL =  getResources().getString(R.string.addtocart);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.getBoolean("message")){
                                        Toast.makeText(getActivity(),"Added to Cart", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getActivity(),"Failed adding to Cart", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(),"Error Connecting to API", Toast.LENGTH_SHORT).show();
                            }
                        });
                        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
                    }
                    else {
                        Toast.makeText(getContext(),"Max Quantity is "+maxQuantity,Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(),"Enter the quantity of product",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //intializing scan object
        qrScan = new com.google.zxing.integration.android.IntentIntegrator(getActivity()).addExtra("PROMPT_MESSAGE","Place QR code inside to scan");

        FloatingActionButton fab_scan = dashboardView.findViewById(R.id.fab_scan);
        fab_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DashboardActivity.checkoutStatus){
                    Toast.makeText(getContext(),"Transaction Processing...",Toast.LENGTH_LONG).show();
                }else{
                    qrScan.forSupportFragment(ScanFragment.this).initiateScan();
                }
                //initiating the qr code scan

            }
        });
        return dashboardView;
    }


    //Getting the scan results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "No Information Available", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    productInformation = new JSONObject(result.getContents());
                    //setting values to textviews
                    productID.setText(productInformation.getString("productID"));
                    productName.setText(productInformation.getString("productName"));
                    productCat.setText(productInformation.getString("productCat"));
                    productSize.setText(productInformation.getString("productSize"));
                    productColor.setText(productInformation.getString("productColor"));
                    productBrand.setText(productInformation.getString("productBrand"));
                    productPrice.setText("Rs. "+productInformation.getString("productPrice"));
                    productDesc.setText(productInformation.getString("productDesc"));
                    Toast.makeText(getActivity(), "Products Scanned", Toast.LENGTH_SHORT).show();
                    scanMessage.setVisibility(View.INVISIBLE);
                    productInfoCard.setVisibility(View.VISIBLE);

                    getProductImage();

                    getScanRecommendedProduct();

                    scanRecCardView.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void getScanRecommendedProduct() {
        JSONObject json = new JSONObject();
        try{
            json.put("productID",productInformation.getString("productID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL =  getResources().getString(R.string.getScanRecommendedProducts);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if((response.getBoolean("message")) && (response.getString("type").equals("scan")) ){
                        System.out.println(response);
                        JSONArray productArray = response.getJSONArray("recommendedProducts");
                        System.out.println(productArray);
                        for (int i=0;i<productArray.length();i++){
                            final JSONObject productObject = productArray.getJSONObject(i);
                            final JSONObject json = new JSONObject();
                            try{
                                json.put("productID",productObject.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String URL =  getResources().getString(R.string.getProductInfo);
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getBoolean("message")){

                                            JSONArray productArrays = response.getJSONArray("productInfo");
                                            for (int i=0;i<productArrays.length();i++){
                                                final JSONObject productObjects = productArrays.getJSONObject(i);
                                                String URL =  getResources().getString(R.string.getProductImage);
                                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
                                                            if(response.getBoolean("message")){
                                                                byte[] encodeByte = Base64.decode(response.getString("details"),Base64.DEFAULT);
                                                                Bitmap productImage = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                                                                rproductImages = productImage;
                                                                Products products = new Products(productObject.getString("id"),productObjects.getString("productname"),productObjects.getString("productprice"),rproductImages);

                                                                productsList.add(products);
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                            else{
                                                                Toast.makeText(getActivity(),"Failed loading Image", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(getActivity(),"Error Connecting to API 1", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
                                            }

                                        }
                                        else{
                                            Toast.makeText(getActivity(),"Failed loading Image", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getActivity(),"Error Connecting to API 2", Toast.LENGTH_SHORT).show();
                                }
                            });
                            RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
                        }

                    }
                    else{
                        Toast.makeText(getActivity(),"No Product On such ID", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error Connecting to API 4", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }



    private void getProductImage() {
        JSONObject json = new JSONObject();
        try{
            json.put("productID",productInformation.getString("productID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL =  getResources().getString(R.string.getProductImage);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("message")){
                        byte[] encodeByte = Base64.decode(response.getString("details"),Base64.DEFAULT);
                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                        productImage.setImageBitmap(imageBitmap);
                    }
                    else{
                        Toast.makeText(getActivity(),"Failed loading Image", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error Connecting to API", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }


}
