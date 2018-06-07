package com.anupbista.store;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder>{

    private List<Products> recommendationItems;
    private Context context;

    public RecommendationAdapter(List<Products> recommendationItems, Context context) {
        this.recommendationItems = recommendationItems;
        this.context = context;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendationlayout,parent,false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        final Products products = recommendationItems.get(position);

        holder.name.setText(products.getProductName());
        holder.price.setText("Rs. "+ products.getProductPrice());
        holder.productImage.setImageBitmap(products.getProductImage());
    }

    @Override
    public int getItemCount() {
        return recommendationItems.size();
    }

    public class RecommendationViewHolder extends RecyclerView.ViewHolder{
        TextView name,price;
        ImageView productImage;

        public RecommendationViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }
}
