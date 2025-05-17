package com.hp.grocerystore.view.adapter;

import static com.hp.grocerystore.utils.Resource.Status.ERROR;
import static com.hp.grocerystore.utils.Resource.Status.LOADING;
import static com.hp.grocerystore.utils.Resource.Status.SUCCESS;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.view.activity.ProductDetailActivity;
import com.hp.grocerystore.viewmodel.WishlistViewModel;
//import com.hp.grocerystore.view.activity.WishlistActivity;

import java.util.List;

//public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
//
//    private Context context;
//    private List<Product> productList;
//
//    public ProductAdapter(Context context, List<Product> productList) {
//        this.context = context;
//        this.productList = productList;
//    }
//
//    @NonNull
//    @Override
//    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
//
//        return new ProductViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
//        Product product = productList.get(position);
//        holder.bind(product);
////        holder.itemView.setOnClickListener(v -> {
////            Intent intent = new Intent(context, ProductDetailActivity.class);
////            intent.putExtra("product", product); // gửi product qua Intent
////            context.startActivity(intent);
////        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return productList.size();
//    }
//
//    public static class ProductViewHolder extends RecyclerView.ViewHolder {
//        ImageView imgProduct, imgFavorite;
//        TextView tvName, tvPrice;
//
//        public ProductViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imgProduct = itemView.findViewById(R.id.img_product);
//            tvName = itemView.findViewById(R.id.tv_product_name);
//            tvPrice = itemView.findViewById(R.id.tv_product_price);
//            imgFavorite = itemView.findViewById(R.id.img_favorite);
//        }
//        public void bind(Product product) {
//            tvName.setText(product.getProductName());
//            tvPrice.setText(product.getPrice() + "đ");
//
//            Glide.with(itemView.getContext())
//                    .load(product.getImageUrl())
//                    .placeholder(R.drawable.placeholder_product)
//                    .error(R.drawable.placeholder_product)
//                    .into(imgProduct);
//
//            // Xử lý sự kiện click icon trái tim
//            imgFavorite.setOnClickListener(v -> {
////                Intent intent = new Intent(itemView.getContext(), WishlistActivity.class);
////                itemView.getContext().startActivity(intent);
//            });
//        }
//    }
//    @SuppressLint("NotifyDataSetChanged")
//    public void updateData(List<Product> newList) {
//        productList.clear();
//        productList.addAll(newList);
//        notifyDataSetChanged();
//    }
//}
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final Context context;
    private List<Product> productList;
    private WishlistViewModel wishlistViewModel;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }
    public ProductAdapter(Context context, List<Product> productList, WishlistViewModel wishlistViewModel) {
        this.context = context;
        this.productList = productList;
        this.wishlistViewModel = wishlistViewModel;
    }

    public void setWishlistViewModel(WishlistViewModel wishlistViewModel) {
        this.wishlistViewModel = wishlistViewModel;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setProductList(List<Product> products) {
        this.productList.clear();
        this.productList.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getProductName());
        holder.tvProductPrice.setText(String.format("%,.0f đ", product.getPrice()));

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_product)
                .into(holder.imgProduct);

        // Gợi ý: Có thể set sự kiện cho nút MUA và icon trái tim tại đây nếu cần
        holder.btnBuy.setOnClickListener(v -> {
            // TODO: xử lý mua hàng
        });

        holder.imgFavorite.setOnClickListener(v -> {
            wishlistViewModel.addWishlist(product.getId());

            wishlistViewModel.getAddWishlistResult().observeForever(response -> {
                if (response.getMessage() != null) {
                    Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                    holder.imgFavorite.setImageResource(R.drawable.ic_heart_filled);
                } else {
                    Toast.makeText(context, "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
                     // icon trái tim đầy
                }
            });
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId()); // gửi product qua Intent
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgFavorite;
        TextView tvProductName, tvProductPrice, btnBuy;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            imgFavorite = itemView.findViewById(R.id.img_favorite);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            btnBuy = itemView.findViewById(R.id.btn_buy);

        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

}