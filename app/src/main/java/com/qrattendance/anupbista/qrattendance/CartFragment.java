package com.qrattendance.anupbista.qrattendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CartFragment extends Fragment {

    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<ProductOnCart> productOnCartList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Cart");
        View cartView =  inflater.inflate(R.layout.fragment_cart,container,false);
        return cartView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productOnCartList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.cardRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        getCartData();
        adapter = new ProductAdapter(this.getActivity(), productOnCartList,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FloatingActionButton fab_scan = view.findViewById(R.id.fab_checkout);
        fab_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkoutIntent = new Intent(getContext(), DashboardActivity.class);
                getContext().startActivity(checkoutIntent);
            }
        });

    }

    public void getCartData() {
        SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(this.getActivity());
        String userName = sharedPreferencesUser.getUsername();
        JSONObject json = new JSONObject();
        try{
            json.put("userName",userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL =  getResources().getString(R.string.getCartData);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("message")){

                        JSONArray productArray = response.getJSONArray("cartData");
                        for (int i=0;i<productArray.length();i++){
                            JSONObject productObject = productArray.getJSONObject(i);
                            ProductOnCart productOnCart = new ProductOnCart(productObject.getString("productID"),productObject.getString("productOnCartID"),productObject.getString("productName"),productObject.getString("productCat"),productObject.getString("productSize"),productObject.getString("productBrand"),
                                    productObject.getString("productColor"),productObject.getString("productPrice"),productObject.getString("productDesc"),productObject.getString("productQuantity"));
                            productOnCartList.add(productOnCart);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("No items in Cart");
                        builder.setNegativeButton("Okay",null).create().show();
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
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }
}