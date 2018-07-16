package com.anupbista.store;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final String CHANNEL_ID = "channelCheckout";

    private TextView customerName, customerEmail;
    public static Boolean checkoutStatus =false;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentDashboard()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
        getCustomerInfo();

        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "CheckoutActivity Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("CheckoutActivity Notification Channel");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void getCustomerInfo(){
        SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(DashboardActivity.this);
        final String userName = sharedPreferencesUser.getUsername();
        JSONObject json = new JSONObject();
        try{
            json.put("userName",userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL =  getResources().getString(R.string.getcustomerinfo);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("message")){
                        customerName = findViewById(R.id.customer_name);
                        customerEmail = findViewById(R.id.customer_email);
                        String fullName = response.getJSONObject("details").getString("first_name")+" "+response.getJSONObject("details").getString("last_name");
                        String username = response.getJSONObject("details").getString("username");
                        customerName.setText(fullName+" ("+username+")");
                        customerEmail.setText(response.getJSONObject("details").getString("email"));
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                        builder.setMessage("Error retrieving user Information");
                        builder.setNegativeButton("Retry",null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DashboardActivity.this,"Error Connecting to API", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(DashboardActivity.this);
            sharedPreferencesUser.remove();
            Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
            DashboardActivity.this.startActivity(loginIntent);
            finish();
            return true;
        }
        else if (id == R.id.action_cart) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.action_menu_cart);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new CartFragment()).commit();
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_cart);
            return true;
        }
        else if (id == R.id.action_scan) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.action_menu);
            FragmentDashboard fragmentDashboard = new FragmentDashboard();
            Bundle bundle = new Bundle();
            bundle.putString("PageNumber","1");
            fragmentDashboard.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragmentDashboard).commit();
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_dashboard);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.action_menu);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentDashboard()).commit();
        }
        else if (id == R.id.nav_cart) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.action_menu_cart);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new CartFragment()).commit();
        }
        else if (id == R.id.nav_order) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.action_menu);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new OrderFragment()).commit();
        }else if (id == R.id.nav_checkDetails) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.action_menu);
            Intent checkDetailsIntent = new Intent(DashboardActivity.this, CheckoutActivity.class);
            DashboardActivity.this.startActivity(checkDetailsIntent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
