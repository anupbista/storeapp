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

import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.orderItemsViewHolder>{

    private List<Orders> orderItems;
    private Context context;
    private OrderFragment fragment;

    public OrderItemsAdapter(Context context, List<Orders> orderItems, OrderFragment fragment) {
        this.orderItems = orderItems;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public OrderItemsAdapter.orderItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlayout,parent,false);
        return new orderItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemsAdapter.orderItemsViewHolder holder, int position) {
        Orders orders = orderItems.get(position);
        holder.orderName.setText(orders.getProductname());
        holder.orderColor.setText(orders.getOrderColor());
        holder.orderPrice.setText("Rs. "+ Double.parseDouble(orders.getOrderPrice())*Double.parseDouble(orders.getProductquantity()));
        holder.orderQuantity.setText(orders.getProductquantity());
        holder.orderSize.setText(orders.getOrderSize());
        holder.orderProductImage.setImageBitmap(orders.getOrderProductImage());
        if (orders.getStatus().equals("pending")){
            holder.orderPending.setVisibility(View.VISIBLE);
            holder.orderProcessing.setVisibility(View.INVISIBLE);
        }else if(orders.getStatus().equals("processing")){
            holder.orderProcessing.setVisibility(View.VISIBLE);
            holder.orderPending.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }


    public class orderItemsViewHolder extends RecyclerView.ViewHolder{

        TextView orderName,orderColor,orderSize,orderPrice,orderQuantity;
        ImageView orderProductImage;
        TextView orderProcessing;
        TextView orderPending;

        public orderItemsViewHolder(View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.orderName);
            orderColor = itemView.findViewById(R.id.orderColor);
            orderSize = itemView.findViewById(R.id.orderSize);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            orderQuantity = itemView.findViewById(R.id.orderQuantity);
            orderProductImage = itemView.findViewById(R.id.orderProductImage);
            orderPending = itemView.findViewById(R.id.orderStatusPending);
            orderProcessing = itemView.findViewById(R.id.orderStatusProcessing);
        }
    }
}
