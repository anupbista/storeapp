package com.anupbista.store;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

public class RecommendationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    RecommendationAdapter adapter;
    List<Products> productsList;
    Bitmap productImage;
    SwipeRefreshLayout swipeRefreshLayout;
    Bitmap productImages;

    public RecommendationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View recommendationView =  inflater.inflate(R.layout.b_nav_home,container,false);
        return recommendationView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productsList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recommendationRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.recoFragmentRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));

        adapter = new RecommendationAdapter(productsList,getContext());
        recyclerView.setAdapter(adapter);
        getCartData();
    }

    public void getCartData() {
        final ProgressDialog progressDialog  = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Recommendations...");
        progressDialog.show();
        SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(this.getActivity());
        String userName = sharedPreferencesUser.getUsername();
        JSONObject json = new JSONObject();

        try{
            json.put("userName",userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL =  getResources().getString(R.string.getrecommendedProducts);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    if((response.getBoolean("message")) && (response.getString("type").equals("bill")) || (response.getString("type").equals("cart"))){
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
                                                                productImages = productImage;
                                                                Products products = new Products(productObject.getString("id"),productObjects.getString("productname"),productObjects.getString("productprice"),productImages);
                                                                if (swipeRefreshLayout.isRefreshing()){
                                                                    swipeRefreshLayout.setRefreshing(false);
                                                                }
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
                    else if((response.getBoolean("message")) && (response.getString("type").equals("random"))){
                        System.out.println(response);
                        JSONArray productArray = response.getJSONArray("recommendedProducts");
                        System.out.println(productArray);
                        for (int i=0;i<productArray.length();i++){
                            final JSONObject productObject = productArray.getJSONObject(i);
                            final JSONObject json = new JSONObject();
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
                                            byte[] encodeByte = Base64.decode(response.getString("details"),Base64.DEFAULT);
                                            Bitmap productImage = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                                            productImages = productImage;
                                            Products products = new Products(productObject.getString("productID"),productObject.getString("productname"),productObject.getString("productprice"),productImages);
                                            if (swipeRefreshLayout.isRefreshing()){
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
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
                        Toast.makeText(getActivity(),"Error Connecting to API 3", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRefresh() {
        productsList.clear();
        recyclerView.setAdapter(null);
        getCartData();
        recyclerView.setAdapter(adapter);
    }
}
