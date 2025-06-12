package com.hp.grocerystore.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.cart.CartItem;
import com.hp.grocerystore.utils.Extensions;
import java.util.List;

public class ConfirmationAdapter extends RecyclerView.Adapter<ConfirmationAdapter.ViewHolder> {
    private final Context context;
    private final List<CartItem> items;

    public ConfirmationAdapter(Context context, List<CartItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_confirmation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.textProductName.setText(item.getProductName());
        holder.textQuantity.setText("Số lượng: " + item.getQuantity());
        holder.textPrice.setText("Đơn giá: " + Extensions.formatCurrency(item.getPrice()));
        holder.textSubtotal.setText("Tổng: " + Extensions.formatCurrency(item.getPrice() * item.getQuantity()));
        Glide.with(context).load(item.getImageUrl()).into(holder.imageProduct);
    }


    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textProductName, textQuantity, textPrice, textSubtotal;
        ImageView imageProduct;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            textPrice = itemView.findViewById(R.id.text_price);
            textSubtotal = itemView.findViewById(R.id.text_subtotal);
            imageProduct = itemView.findViewById(R.id.image_product);
        }
    }
}