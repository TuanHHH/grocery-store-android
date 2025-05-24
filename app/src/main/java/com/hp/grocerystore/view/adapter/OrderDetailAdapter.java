package com.hp.grocerystore.view.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.hp.grocerystore.view.activity.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ProductViewHolder> {

    private List<ProductOrder> products;
    private Context context;

    public OrderDetailAdapter(List<ProductOrder> products) {
        this.products = products != null ? products : new ArrayList<>();
        this.context = null;
    }

    public void updateProductOrders(List<ProductOrder> productOrders) {
        this.products.clear();
        if (productOrders != null) {
            this.products.addAll(productOrders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_in_order_detail, parent, false);
        this.context = parent.getContext();
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductOrder product = products.get(position);
        holder.productName.setText(product.getProductName());

//        holder.productPrice.setText(String.format("$%.2f", product.getUnitPrice()));
        holder.productPrice.setText(Extensions.formatCurrency(product.getUnitPrice()));
        holder.productQuantity.setText("Số lượng: " + product.getQuantity());
        Glide.with(holder.itemView.getContext()).load(product.getImageUrl()).into(holder.productImage);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            long productId = (long) product.getProductId();
            intent.putExtra("product_id", productId);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }
}