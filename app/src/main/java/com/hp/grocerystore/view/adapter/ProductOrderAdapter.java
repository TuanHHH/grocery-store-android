package com.hp.grocerystore.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.product.ProductOrder;
import com.hp.grocerystore.utils.Extensions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ViewHolder> {

    private List<ProductOrder> productOrders;

    public ProductOrderAdapter(List<ProductOrder> productOrders) {
        this.productOrders = productOrders;
    }

    public void updateProductOrders(List<ProductOrder> newOrders) {
        this.productOrders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_in_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductOrder productOrder = productOrders.get(position);

        // Load image using Glide (if image URL is available)
        if (productOrder != null && productOrder.getImageUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(productOrder.getImageUrl())
                    .placeholder(R.drawable.product_placeholder)
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.product_placeholder);
        }

        holder.productName.setText(productOrder.getProductName());
//        holder.productPrice.setText(String.format("$%.2f", productOrder.getUnitPrice()));
        holder.productPrice.setText(Extensions.formatCurrency(productOrder.getUnitPrice()));
        holder.productQuantity.setText("Số lượng: " + productOrder.getQuantity());
    }

    @Override
    public int getItemCount() {
        return productOrders != null ? productOrders.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
        }
    }
}