package com.anupbista.store;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class OrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView orderRecyclerview;
    OrderItemsAdapter adapter;
    List<Orders> orderList;
    ImageView emptyOrderMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    Bitmap orderProductImages;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Your Orders");
        View orderView =  inflater.inflate(R.layout.fragment_order,container,false);

        return orderView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderList = new ArrayList<>();
        orderRecyclerview = view.findViewById(R.id.orderRecyclerview);
        swipeRefreshLayout = view.findViewById(R.id.orderFragmentRefresh);
        emptyOrderMessage = view.findViewById(R.id.emptyOrderMessage);

        swipeRefreshLayout.setOnRefreshListener(this);

        orderRecyclerview.setHasFixedSize(true);
        orderRecyclerview.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter = new OrderItemsAdapter(this.getActivity(), orderList,this);
        orderRecyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getOrdersData();

    }

    public void getOrdersData() {
        final ProgressDialog progressDialog  = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Orders...");
        progressDialog.show();

        SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(this.getActivity());
        String userName = sharedPreferencesUser.getUsername();
        JSONObject json = new JSONObject();

        try{
            json.put("userName",userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL =  getResources().getString(R.string.getOrderData);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    if(response.getBoolean("message")){
                        System.out.println(response);
                        JSONArray productArray = response.getJSONArray("orderData");
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
                                            byte[] encodeByte = Base64.decode(response.getString("details"),Base64.DEFAULT);
                                            Bitmap orderProductImage = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                                            orderProductImages = orderProductImage;
                                            Orders orders = new Orders(productObject.getString("productname"),productObject.getString("productQuantity"),productObject.getString("productprice"),productObject.getString("productsize"),productObject.getString("productcolor"),orderProductImages,productObject.getString("status"));
                                            if(swipeRefreshLayout.isRefreshing()){
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                            orderList.add(orders);
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
                                    Toast.makeText(getActivity(),"Error Connecting to API", Toast.LENGTH_SHORT).show();
                                }
                            });
                            RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
                        }
                    }
                    else{
                        if(swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        emptyOrderMessage.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(),"No Orders", Toast.LENGTH_SHORT).show();
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
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public void onRefresh() {
        orderList.clear();
        orderRecyclerview.setAdapter(null);
        getOrdersData();
        orderRecyclerview.setAdapter(adapter);
    }
}