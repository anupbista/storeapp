package com.qrattendance.anupbista.qrattendance;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    private Context mCtx;
    private List<ProductOnCart> productOnCartList;
    private CartFragment fragment;

    public ProductAdapter(Context mCtx, List<ProductOnCart> productOnCartList,CartFragment fragment) {
        this.mCtx = mCtx;
        this.productOnCartList = productOnCartList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.cartlayout,null);
        return  new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, final int position) {
        final ProductOnCart productOnCart = productOnCartList.get(position);

        holder.name.setText(productOnCart.getProductName());
        holder.category.setText(productOnCart.getProductCat());
        holder.color.setText(productOnCart.getProductColor());
        holder.size.setText(productOnCart.getProductSize());
        holder.price.setText(productOnCart.getProductPrice());
        holder.desc.setText(productOnCart.getProductDesc());
        holder.quantity.setText(productOnCart.getProductQuantity());
        holder.removeFromCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try{
                    json.put("productOnCartID", productOnCart.getProductOnCartID());
                    json.put("productID", productOnCart.getProductID());
                    json.put("productQuantity", productOnCart.getProductQuantity());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String URL =  mCtx.getResources().getString(R.string.removeFromCart);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("message")){
                                Toast.makeText(mCtx,"Removed from Cart", Toast.LENGTH_SHORT).show();
                                productOnCartList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, productOnCartList.size());
                            }
                            else{
                                Toast.makeText(mCtx,"Failed removing from Cart", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx,"Error Connecting to API", Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueueSingleton.getInstance(mCtx).addToRequestQueue(jsonObjectRequest);

            }
        });
    }

    @Override
    public int getItemCount() {
        return productOnCartList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView name, category,color,size,brand, price,desc,quantity;
        ImageButton removeFromCartBtn;

        public ProductViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
            color = itemView.findViewById(R.id.color);
            size = itemView.findViewById(R.id.size);
            brand = itemView.findViewById(R.id.brand);
            price = itemView.findViewById(R.id.price);
            desc = itemView.findViewById(R.id.desc);
            quantity = itemView.findViewById(R.id.quantity);
            removeFromCartBtn = itemView.findViewById(R.id.removeFromCartBtn);
        }
    }
}
