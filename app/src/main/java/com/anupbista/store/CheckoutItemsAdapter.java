package com.anupbista.store;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CheckoutItemsAdapter extends RecyclerView.Adapter<CheckoutItemsAdapter.CheckoutItemsViewHolder>{

    private List<CheckoutItemsBill> CheckoutItems;
    private Context context;

    public CheckoutItemsAdapter(List<CheckoutItemsBill> checkoutItems, Context context) {
        CheckoutItems = checkoutItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CheckoutItemsAdapter.CheckoutItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemscheckoutbill,parent,false);
        return new CheckoutItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemsAdapter.CheckoutItemsViewHolder holder, int position) {
        CheckoutItemsBill checkoutItemsBill = CheckoutItems.get(position);
        holder.productIDBill.setText(checkoutItemsBill.getProductIDBill());
        holder.productNameBill.setText(checkoutItemsBill.getProductNameBill());
        holder.productQuantityBill.setText(checkoutItemsBill.getProductQuantityBill());
        holder.productPriceBill.setText("Rs: "+checkoutItemsBill.getProductPriceBill());
    }

    @Override
    public int getItemCount() {
        return CheckoutItems.size();
    }


    public class CheckoutItemsViewHolder extends RecyclerView.ViewHolder{

        public TextView productIDBill;
        public TextView productNameBill;
        public TextView productQuantityBill;
        public TextView productPriceBill;

        public CheckoutItemsViewHolder(View itemView) {
            super(itemView);
            productIDBill = itemView.findViewById(R.id.productIDBill);
            productNameBill = itemView.findViewById(R.id.productNameBill);
            productQuantityBill = itemView.findViewById(R.id.productQuantityBill);
            productPriceBill = itemView.findViewById(R.id.productPriceBill);
        }
    }
}
