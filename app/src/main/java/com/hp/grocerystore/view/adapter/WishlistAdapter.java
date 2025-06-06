package com.hp.grocerystore.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.wishlist.Wishlist;

import java.util.ArrayList;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private final Context context;
    private List<Wishlist> wishList = new ArrayList<>();
    private OnWishlistItemClickListener listener;

    public void setOnWishlistItemClickListener(OnWishlistItemClickListener listener) {
        this.listener = listener;
    }

    public WishlistAdapter(Context context, List<Wishlist> wishList) {
        this.context = context;
        this.wishList = wishList;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        Wishlist product = wishList.get(position);
        holder.bind(product);
        holder.imgFavorite.setOnClickListener(v -> showDeleteDialog(holder.itemView.getContext(), holder.getAdapterPosition()));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Wishlist> newList) {
        wishList.clear();
        wishList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgFavorite;
        TextView tvName, tvPrice;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            imgFavorite = itemView.findViewById(R.id.img_favorite);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Wishlist wishlist) {
            tvName.setText(wishlist.getProductName());
            tvPrice.setText(wishlist.getPrice() + "đ");
            String imageUrl = wishlist.getImageUrl();
            if (imageUrl != null && (imageUrl.endsWith(".jpg") || imageUrl.endsWith(".png") || imageUrl.endsWith(".jpeg"))) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.category_placeholder)
                        .into(imgProduct);
            } else {
                imgProduct.setImageResource(R.drawable.category_placeholder);
            }
        }
    }

    public interface OnWishlistItemClickListener {
        void onRemoveClick(int position);
        void onItemClick(int position);
    }
    private void showDeleteDialog(Context context, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(position);
            }
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

}
