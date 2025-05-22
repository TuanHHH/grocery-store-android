package com.hp.grocerystore.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.grocerystore.R;
import com.hp.grocerystore.model.order.Order;
import com.hp.grocerystore.utils.Extensions;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private final OnOrderClickListener listener;

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders != null ? orders : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderId.setText("Order #" + order.getId());
        holder.orderStatus.setText("Status: " + getStatusText(order.getStatus()));
        holder.orderTotal.setText(Extensions.formatCurrency(order.getTotalPrice()));
//        holder.orderTotal.setText(String.format("Total: $%.2f", order.getTotalPrice()));
        holder.orderTime.setText("Ordered: " + formatOrderTime(order.getOrderTime()));
        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders.clear();
        if (newOrders != null) {
            this.orders.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    private String getStatusText(int status) {
        switch (status) {
            case 0:
                return "Pending";
            case 1:
                return "In delivery";
            case 2:
                return "Success";
            case 3:
                return "Canceled";
            default:
                return "Unknown";
        }
    }

private String formatOrderTime(String orderTime) {
    try {
        // Định dạng đầu vào ISO 8601
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Vì chuỗi có 'Z' = UTC

        Date date = inputFormat.parse(orderTime);

        // Định dạng đầu ra: giây:phút:giờ ngày/tháng/năm
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        outputFormat.setTimeZone(TimeZone.getDefault()); // Giờ địa phương

        return outputFormat.format(date);
    } catch (Exception e) {
        return orderTime;
    }
}

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderStatus, orderTotal, orderTime;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderTime = itemView.findViewById(R.id.orderTime);
        }
    }

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }
}