package com.hp.grocerystore.view.adapter;

import android.annotation.SuppressLint;
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
//import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.model.wishlist.Wishlist;

import java.util.ArrayList;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private Context context;
    private List<Wishlist> wishList = new ArrayList<>();
//    private OnWishlistItemClickListener listener;

//    public interface OnWishlistItemClickListener {
//        void onRemoveClick(Product product);
//    }

    public WishlistAdapter(Context context, List<Wishlist> wishList) {
        this.context = context;
        this.wishList = wishList;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setWishList(List<Wishlist> wishList) {
        this.wishList.clear();
        this.wishList.addAll(wishList);
        notifyDataSetChanged();
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

        holder.imgFavorite.setOnClickListener(v -> {
//            listener.onRemoveClick(product);
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

        public void bind(Wishlist product) {
            tvName.setText(product.getProductName());
            tvPrice.setText(product.getPrice() + "đ");

            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .into(imgProduct);
        }
    }
}
