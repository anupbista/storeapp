package com.anupbista.store;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class CheckoutService extends Service{

    private NotificationManagerCompat notificationManagerCompat;
    private Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManagerCompat = NotificationManagerCompat.from(this);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(getApplicationContext());
                JSONObject json = new JSONObject();
                try {
                    json.put("userName",sharedPreferencesUser.getUsername());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String URL =  getResources().getString(R.string.staffcheckout);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("message")){
                                System.out.println("listening to server for payment");
                                JSONArray productArray = response.getJSONArray("status");
                                int status=0;
                                for (int i=0;i<productArray.length();i++){
                                    JSONObject productObject = productArray.getJSONObject(i);
                                    status = Integer.parseInt(productObject.getString("staffCheckout"));
                                }
                                if (status == 1){
                                    //send notification to customer for successful payment

                                    Intent checkoutIntent = new Intent(getApplicationContext(), CheckoutActivity.class);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                            0, checkoutIntent,0);

                                    Notification notification = new Notification.Builder(getApplicationContext(), DashboardActivity.CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_checkout)
                                            .setContentTitle("Checkout Completed")
                                            .setContentText("The payment fot your cart was successfully made")
                                            .setPriority(Notification.PRIORITY_HIGH)
                                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true)
                                            .build();
                                    notificationManagerCompat.notify(1,notification);

                                    DashboardActivity.checkoutStatus = false;

                                    timer.cancel();
                                    timer.purge();

                                    getApplicationContext().sendBroadcast(new Intent("com.anupbista.store.CHECKOUT_NOTI"));


                                    SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(getApplicationContext());
                                    JSONObject json = new JSONObject();
                                    try {
                                        json.put("userName",sharedPreferencesUser.getUsername());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String URL =  getResources().getString(R.string.updateCheckout);
                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getBoolean("message")){
                                                    Log.d("Checkout Status","Updated checkout status: removed user from active list");
                                                }
                                                else{
                                                    Log.d("Checkout Status","Could not update checkout status");
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });
                                    RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Failed to make payment", Toast.LENGTH_SHORT).show();
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
        },0,5000);
        stopSelf();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
