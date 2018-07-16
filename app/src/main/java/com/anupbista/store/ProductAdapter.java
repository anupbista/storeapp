package com.anupbista.store;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private List<Products> productsList;
    private CartFragment fragment;

    public ProductAdapter(Context mCtx, List<Products> productsList, CartFragment fragment) {
        this.mCtx = mCtx;
        this.productsList = productsList;
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
        final Products products = productsList.get(position);

        holder.name.setText(products.getProductName());
        holder.category.setText(products.getProductCat());
        holder.color.setText(products.getProductColor());
        holder.size.setText(products.getProductSize());
        holder.price.setText("Rs. "+ products.getProductPrice());
        holder.desc.setText(products.getProductDesc());
        holder.brand.setText(products.getProductBrand());
        holder.quantity.setText(products.getProductQuantity());
        holder.productImage.setImageBitmap(products.getProductImage());
        if (products.getHomedelivery().equals("1")){
            holder.homedeliveryLayout.setVisibility(View.VISIBLE);
        }else{
            holder.homedeliveryLayout.setVisibility(View.INVISIBLE);
        }
        holder.removeFromCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUser sharedPreferencesUser = new SharedPreferencesUser(mCtx);
                JSONObject json = new JSONObject();
                try{
                    json.put("productOnCartID", products.getProductOnCartID());
                    json.put("productID", products.getProductID());
                    json.put("productQuantity", products.getProductQuantity());
                    json.put("userName", sharedPreferencesUser.getUsername());
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
                                productsList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, productsList.size());
                                fragment.calculateTotalPrice();
                                if (productsList.isEmpty()){
                                    fragment.cartDetailsLayout.setVisibility(View.INVISIBLE);
                                    fragment.emptyCartMessage.setVisibility(View.VISIBLE);
                                }
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
        return productsList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView name, category,color,size,brand, price,desc,quantity;
        LinearLayout homedeliveryLayout;
        ImageButton removeFromCartBtn;
        ImageView productImage;

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
            productImage = itemView.findViewById(R.id.productImage);
            homedeliveryLayout = itemView.findViewById(R.id.homedeliveryLayout);
        }
    }
}
