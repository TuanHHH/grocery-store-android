package com.hp.grocerystore.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.hp.grocerystore.model.cart.CartItem;
import com.hp.grocerystore.R;
import com.hp.grocerystore.utils.Extensions;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    public interface CartActionListener {
        void onIncreaseQuantity(int position);
        void onDecreaseQuantity(int position);
        void onDeleteItem(int position);
        void onSelectionChanged(); // new callback for selection changes
    }

    private Context context;
    private List<CartItem> cartItems;
    private CartActionListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems, CartActionListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CartItem item = cartItems.get(position);
        holder.productName.setText(item.getProductName());
        holder.inventoryQuantity.setText(String.format("Có sẵn: %d", item.getInventoryQuantity()));
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        Glide.with(context).load(item.getImageUrl()).into(holder.productImage);
        holder.productPrice.setText(Extensions.formatCurrency(item.getPrice()));

        if (item.getInventoryQuantity() <= 0) {
            holder.textOutOfStock.setVisibility(View.VISIBLE);
        } else {
            holder.textOutOfStock.setVisibility(View.GONE);
        }

        // Xử lý trạng thái hết hàng
        handleOutOfStockState(holder, item, position);

        holder.buttonPlus.setOnClickListener(v -> listener.onIncreaseQuantity(position));
        holder.buttonMinus.setOnClickListener(v -> listener.onDecreaseQuantity(position));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteItem(position));

        // Disable plus if quantity >= inventory
        holder.buttonPlus.setEnabled(item.getQuantity() < item.getInventoryQuantity());
        // Disable minus if quantity <= 1
        holder.buttonMinus.setEnabled(item.getQuantity() > 1);

        return convertView;
    }

    static class ViewHolder {
        View rootView;
        ImageView productImage;
        TextView productName, inventoryQuantity, quantity, productPrice;
        MaterialButton buttonPlus, buttonMinus;
        ImageButton buttonDelete;
        android.widget.CheckBox checkBox;
        TextView textOutOfStock;
        ViewHolder(View view) {
            rootView = view;
            checkBox = view.findViewById(R.id.checkbox_select);
            productImage = view.findViewById(R.id.image_product);
            productName = view.findViewById(R.id.text_product_name);
            productPrice = view.findViewById(R.id.text_product_price);
            inventoryQuantity = view.findViewById(R.id.text_inventory_quantity);
            quantity = view.findViewById(R.id.text_quantity);
            buttonPlus = view.findViewById(R.id.button_plus);
            buttonMinus = view.findViewById(R.id.button_minus);
            buttonDelete = view.findViewById(R.id.button_delete);
            textOutOfStock = view.findViewById(R.id.text_out_of_stock);
        }
    }

    private void handleOutOfStockState(ViewHolder holder, CartItem item, int position) {
        if (item.getInventoryQuantity() == 0) {
            holder.checkBox.setEnabled(false);
            holder.checkBox.setChecked(false);
            // Làm mờ item
            holder.rootView.setAlpha(0.5f);
            holder.buttonDelete.setVisibility(View.VISIBLE);
            holder.buttonDelete.setEnabled(true);
            holder.buttonDelete.setOnClickListener(v -> listener.onDeleteItem(position));
        } else {
            holder.checkBox.setEnabled(true);
            holder.rootView.setAlpha(1f);
            holder.buttonDelete.setVisibility(View.VISIBLE);
            holder.buttonDelete.setEnabled(true);
            holder.buttonDelete.setOnClickListener(v -> listener.onDeleteItem(position));
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setSelected(isChecked);
                if (listener != null) listener.onSelectionChanged();
            });
        }
    }
}
