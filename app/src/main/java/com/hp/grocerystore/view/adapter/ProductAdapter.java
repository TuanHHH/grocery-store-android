package com.hp.grocerystore.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.view.activity.ProductDetailActivity;
import com.hp.grocerystore.viewmodel.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final Context context;
    private List<Product> productList;
    private static List<Wishlist> wishlistList = new ArrayList<>();
    private LifecycleOwner lifecycleOwner;

    public ProductAdapter(Context context, List<Product> productList,LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.productList = productList;
        this.lifecycleOwner = lifecycleOwner;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setProductList(List<Product> products) {
        this.productList.clear();
        this.productList.addAll(products);
        notifyDataSetChanged();
    }
    public static void setWishList(List<Wishlist> wishlistList){
        if (ProductAdapter.wishlistList.size() > 0) ProductAdapter.wishlistList.clear();
        ProductAdapter.wishlistList.addAll(wishlistList);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getProductName());
        holder.tvProductPrice.setText(String.format("%,.0f đ", product.getPrice()));
        if (product.getQuantity() < 40 && product.getQuantity() > 0){
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText("Còn " + product.getQuantity() +" " + (product.getUnit() != null ? product.getUnit(): " suất"));
        } else if(product.getQuantity() == 0){
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText("Hết hàng");
            holder.tvQuantity.setTextColor(context.getResources().getColor(R.color.gray));
            holder.tvQuantity.setBackgroundResource(R.drawable.bg_search_rounded);
        }
        else {
            holder.tvQuantity.setVisibility(View.INVISIBLE);
        }

        String imageUrl = product.getImageUrl();
        if (imageUrl != null && (imageUrl.endsWith(".jpg") || imageUrl.endsWith(".png") || imageUrl.endsWith(".jpeg"))) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_product)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder_product);
        }

        float rating = product.getRating();
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5f;

        for (int i = 0; i < 5; i++) {
            if (i < fullStars) {
                holder.stars[i].setImageResource(R.drawable.ic_star_yellow);
            } else if (i == fullStars && hasHalfStar) {
                holder.stars[i].setImageResource(R.drawable.ic_star_half); // nửa sao
            } else {
                holder.stars[i].setImageResource(R.drawable.ic_star_gray);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgFavorite;
        TextView tvProductName, tvProductPrice,tvQuantity;
        ImageView[] stars = new ImageView[5];

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            imgFavorite = itemView.findViewById(R.id.img_favorite);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);

            stars[0] = itemView.findViewById(R.id.star_1);
            stars[1] = itemView.findViewById(R.id.star_2);
            stars[2] = itemView.findViewById(R.id.star_3);
            stars[3] = itemView.findViewById(R.id.star_4);
            stars[4] = itemView.findViewById(R.id.star_5);

        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

}