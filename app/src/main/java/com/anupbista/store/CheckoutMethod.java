package com.anupbista.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckoutMethod extends BottomSheetDialogFragment {

    private Button btn_homedelivery;
    private Button btn_self;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkout_method_layout, container, false);

        btn_homedelivery = view.findViewById(R.id.btn_homedelivery);
        btn_self = view.findViewById(R.id.btn_self);

        btn_self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutBtn.setEnabled(false);
                SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(getContext());
                JSONObject json = new JSONObject();
                try {
                    json.put("userName",sharedPreferencesUser.getUsername());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String URL =  getResources().getString(R.string.customercheckout);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("message")){
                                Toast.makeText(getActivity(),"Marked for CheckoutActivity", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(),"GO to counter for payment", Toast.LENGTH_LONG).show();
                                checkoutBtn.setText("PROCESSING");
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
            }
        });

        return view;
    }
}
