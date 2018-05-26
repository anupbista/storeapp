package com.qrattendance.anupbista.qrattendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentDashboard extends Fragment {

    private TextView productID, productName, productCat, productSize, productColor, productBrand, productPrice, productDesc, scanMessage;
    private CardView productInfoCard;
    private Button addToCartBtn;
    private EditText productQuantity;
    JSONObject productInformation;
    //qr code scanner object
    private IntentIntegrator qrScan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Dashboard");
        View dashboardView =  inflater.inflate(R.layout.fragment_dashboard,container,false);

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

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(v.getContext());
                JSONObject json = new JSONObject();
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                String time = sdf.format(d);
                String currentTimeDate = DateFormat.getDateTimeInstance().format(new Date());

                try{
                    json.put("userName",sharedPreferencesUser.getUsername());
                    json.put("productOnCartID",sharedPreferencesUser.getUsername()+productInformation.getString("productID")+time);
                    json.put("productName",productInformation.getString("productName"));
                    json.put("productID",productInformation.getString("productID"));
                    json.put("productCat",productInformation.getString("productCat"));
                    json.put("productSize",productInformation.getString("productSize"));
                    json.put("productBrand",productInformation.getString("productBrand"));
                    json.put("productColor",productInformation.getString("productColor"));
                    json.put("productPrice",productInformation.getString("productPrice"));
                    json.put("productDesc",productInformation.getString("productDesc"));
                    json.put("productQuantity",productQuantity.getText().toString());
                    json.put("productAddedDateTime",currentTimeDate);
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
        });
        //intializing scan object
        qrScan = new com.google.zxing.integration.android.IntentIntegrator(getActivity()).setPrompt("Place QR Code within view to scan");

        FloatingActionButton fab_scan = dashboardView.findViewById(R.id.fab_scan);
        fab_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initiating the qr code scan
                qrScan.forSupportFragment(FragmentDashboard.this).initiateScan();
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
                    productPrice.setText(productInformation.getString("productPrice"));
                    productDesc.setText(productInformation.getString("productDesc"));
                    Toast.makeText(getActivity(), "ProductOnCart Scanned", Toast.LENGTH_SHORT).show();
                    scanMessage.setVisibility(View.INVISIBLE);
                    productInfoCard.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
