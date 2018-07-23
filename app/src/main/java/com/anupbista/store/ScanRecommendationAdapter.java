package com.anupbista.store;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ScanRecommendationAdapter extends RecyclerView.Adapter<ScanRecommendationAdapter.RecommendationViewHolder>{

    private List<Products> recommendationItems;
    private Context context;

    public ScanRecommendationAdapter(List<Products> recommendationItems, Context context) {
        this.recommendationItems = recommendationItems;
        this.context = context;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scanrecommendationlayout,parent,false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        final Products products = recommendationItems.get(position);

        holder.name.setText(products.getProductName());
        holder.price.setText("Rs. "+ products.getProductPrice());
        holder.productImage.setImageBitmap(products.getProductImage());
        holder.recCardView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent productDetailIntent = new Intent(context,ProductDetail.class);
                productDetailIntent.putExtra("productID", products.getProductID());
                System.out.println(products.getProductID());
                context.startActivity(productDetailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recommendationItems.size();
    }

    public class RecommendationViewHolder extends RecyclerView.ViewHolder{
        TextView name,price;
        ImageView productImage;
        public CardView recCardView;

        public RecommendationViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sname);
            price = itemView.findViewById(R.id.sprice);
            productImage = itemView.findViewById(R.id.sproductImage);
            recCardView = itemView.findViewById(R.id.srecCardView);
        }
    }
}
