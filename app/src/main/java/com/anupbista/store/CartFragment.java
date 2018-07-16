package com.anupbista.store;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class CartFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<Products> productsList;
    Bitmap productImage;
    Bitmap productImages;
    TextView totalCart;
    LinearLayout cartDetailsLayout;
    ImageView emptyCartMessage;
    ProgressBar checkoutProgress;
    BroadcastReceiver broadNoti;
    double total= 0;
    Button checkoutBtn;
    private String paymentSelection;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadNoti = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkoutProgress.setVisibility(View.INVISIBLE);
                getCartData();
                cartDetailsLayout.setVisibility(View.INVISIBLE);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Your Cart");
        View cartView =  inflater.inflate(R.layout.fragment_cart,container,false);

        return cartView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadNoti);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(broadNoti, new IntentFilter("com.anupbista.store.CHECKOUT_NOTI"));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productsList = new ArrayList<>();
        checkoutBtn = view.findViewById(R.id.checkoutBtn);
        recyclerView = view.findViewById(R.id.cardRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter = new ProductAdapter(this.getActivity(), productsList,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        totalCart = view.findViewById(R.id.cartTotal);
        cartDetailsLayout = view.findViewById(R.id.cartDetailsLayout);
        emptyCartMessage = view.findViewById(R.id.emptyCartMessage);
        checkoutProgress = view.findViewById(R.id.checkoutProgress);
        swipeRefreshLayout = view.findViewById(R.id.cartFragmentRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        getCartData();

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Checkout");
                builder.setMessage(R.string.checkout_sure);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paymentSelection = "Cash";
                        final String[] payment = getActivity().getResources().getStringArray(R.array.payment);

                        AlertDialog.Builder pbuilder = new AlertDialog.Builder(getContext());
                        pbuilder.setTitle("Payment");
                        pbuilder.setSingleChoiceItems(R.array.payment, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                paymentSelection = payment[which];
                            }
                        });

                        pbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                checkoutBtn.setEnabled(false);

                                if (paymentSelection.equals("Cash")){
                                    SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(getContext());
                                    JSONObject json = new JSONObject();
                                    try {
                                        json.put("userName",sharedPreferencesUser.getUsername());
                                        json.put("paymentmethod",paymentSelection);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String URL =  getResources().getString(R.string.customercheckout);
                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getBoolean("message")){
                                                    Toast.makeText(getActivity(),"Marked for Checkout", Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(getActivity(),"GO to counter for payment", Toast.LENGTH_LONG).show();
                                                    checkoutBtn.setText("Processing");
                                                    DashboardActivity.checkoutStatus = true;

                                                    recyclerView.setVisibility(View.INVISIBLE);
                                                    checkoutProgress.setVisibility(View.VISIBLE);

                                                    Intent i = new Intent(getContext(),CheckoutService.class);
                                                    getContext().startService(i);

                                                }
                                                else{
                                                    Toast.makeText(getActivity(),"Failed to mark for CheckoutActivity", Toast.LENGTH_SHORT).show();
                                                    checkoutBtn.setEnabled(true);

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
                                    Toast.makeText(getContext(),"CheckoutCompleted",Toast.LENGTH_SHORT).show();

                                }else if (paymentSelection.equals("esewa")){

                                }else if (paymentSelection.equals("khalti")){

                                }



                            }
                        });
                        pbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        pbuilder.show();




                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                             do nothing for cancel
                    }
                });
                AlertDialog dialog = builder.show();
            }
        });

    }

    public void getCartData() {
        final ProgressDialog progressDialog  = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Cart...");
        progressDialog.show();
        if (DashboardActivity.checkoutStatus){
            checkoutBtn.setText("PROCESSING");
            checkoutBtn.setEnabled(false);
            recyclerView.setVisibility(View.INVISIBLE);
            checkoutProgress.setVisibility(View.VISIBLE);
        }
        SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(this.getActivity());
        String userName = sharedPreferencesUser.getUsername();
        JSONObject json = new JSONObject();
        total= 0;
        try{
            json.put("userName",userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL =  getResources().getString(R.string.getCartData);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    if(response.getBoolean("message")){
                        System.out.println(response);
                        cartDetailsLayout.setVisibility(View.VISIBLE);
                        checkoutBtn.setEnabled(true);
                        JSONArray productArray = response.getJSONArray("cartData");
                        for (int i=0;i<productArray.length();i++){
                            final JSONObject productObject = productArray.getJSONObject(i);
                            JSONObject json = new JSONObject();
                            try{
                                json.put("productID",productObject.getString("productID"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            total += Double.parseDouble(productObject.getString("productPrice"));
                            String URL =  getResources().getString(R.string.getProductImage);
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getBoolean("message")){
                                            byte[] encodeByte = Base64.decode(response.getString("details"),Base64.DEFAULT);
                                            Bitmap productImage = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                                            productImages = productImage;
                                            Products products = new Products(productObject.getString("productID"),productObject.getString("productOnCartID"),productObject.getString("productName"),productObject.getString("productCat"),productObject.getString("productSize"),productObject.getString("productBrand"),
                                                    productObject.getString("productColor"),productObject.getString("productPrice"),productObject.getString("productDesc"),productObject.getString("productQuantity"),productImages,productObject.getString("homedelivery"));
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
                                    Toast.makeText(getActivity(),"Error Connecting to API", Toast.LENGTH_SHORT).show();
                                }
                            });
                            RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
                        }
                        totalCart.setText("Rs: "+String.valueOf(total));
                    }
                    else{
                        if (swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        cartDetailsLayout.setVisibility(View.INVISIBLE);
                        emptyCartMessage.setVisibility(View.VISIBLE);
                        checkoutBtn.setEnabled(false);

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

    public void calculateTotalPrice() {
        SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(this.getActivity());
        String userName = sharedPreferencesUser.getUsername();
        JSONObject json = new JSONObject();
        total= 0;
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
                        checkoutBtn.setEnabled(true);
                        JSONArray productArray = response.getJSONArray("cartData");
                        for (int i=0;i<productArray.length();i++) {
                            final JSONObject productObject = productArray.getJSONObject(i);
                            total += Double.parseDouble(productObject.getString("productPrice"));
                        }
                        totalCart.setText("Rs: "+String.valueOf(total));
                    }
                    else{
                        totalCart.setText("Rs: 0.0");
                        checkoutBtn.setEnabled(false);
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

    @Override
    public void onRefresh() {
        productsList.clear();
        recyclerView.setAdapter(null);
        getCartData();
        recyclerView.setAdapter(adapter);
    }
}