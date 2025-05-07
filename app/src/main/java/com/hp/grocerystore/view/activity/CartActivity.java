package com.hp.grocerystore.view.activity;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hp.grocerystore.utils.FormatData;
import com.hp.grocerystore.view.adapter.CartAdapter;
import com.hp.grocerystore.model.CartItem;
import com.hp.grocerystore.R;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    CartAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cart), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup ListView and Adapter
        ListView listView = findViewById(R.id.list_cart);
        android.widget.TextView textTotalProducts = findViewById(R.id.text_total_products);
        android.widget.TextView textTotalPrice = findViewById(R.id.text_total_price);
        android.widget.Button buttonCheckout = findViewById(R.id.button_checkout);
        android.widget.ImageButton buttonBack = findViewById(R.id.button_back);
        android.widget.CheckBox checkBoxSelectAll = findViewById(R.id.checkbox_select_all);
        // Dummy data for demonstration
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem("Apple", "https://cdn-icons-png.flaticon.com/256/7078/7078312.png", 0, 2, 10000));
        cartItems.add(new CartItem("Banana", "https://cdn-icons-png.flaticon.com/256/7078/7078312.png", 5, 1, 15000));
        cartItems.add(new CartItem("Orange", "https://cdn-icons-png.flaticon.com/256/7078/7078312.png", 8, 3, 13000));
        cartItems.add(new CartItem("Apple", "https://cdn-icons-png.flaticon.com/256/7078/7078312.png", 10, 2, 12345));

        adapter = new CartAdapter(this, cartItems, new CartAdapter.CartActionListener() {
            @Override
            public void onIncreaseQuantity(int position) {
                CartItem item = cartItems.get(position);
                if (item.getQuantity() < item.getInventoryQuantity()) {
                    item.setQuantity(item.getQuantity() + 1);
                    adapter.notifyDataSetChanged();
                    updateFooter();
                }
            }
            @Override
            public void onDecreaseQuantity(int position) {
                CartItem item = cartItems.get(position);
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    adapter.notifyDataSetChanged();
                    updateFooter();
                }
            }
            @Override
            public void onDeleteItem(int position) {
                cartItems.remove(position);
                adapter.notifyDataSetChanged();
                updateHeader();
                updateFooter();
            }
            @Override
            public void onSelectionChanged() {
                updateFooter();
                syncSelectAllCheckbox();
            }
        });
        listView.setAdapter(adapter);
        updateHeader();
        updateFooter();
        syncSelectAllCheckbox();

        buttonBack.setOnClickListener(v -> finish());
        buttonCheckout.setOnClickListener(v -> {
            double total = 0.0;
            for (CartItem item : cartItems) {
                if (item.isSelected()) {
                    total += item.getPrice() * item.getQuantity();
                }
            }
            android.widget.Toast.makeText(this, "Thanh toán: " + FormatData.formatCurrency(total), android.widget.Toast.LENGTH_SHORT).show();
        });

        checkBoxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Prevent recursion: only act if user toggled
            if (checkBoxSelectAll.isPressed() || checkBoxSelectAll.isFocused()) {
                for (CartItem item : cartItems) {
                    item.setSelected(isChecked);
                }
                adapter.notifyDataSetChanged();
                updateFooter();
            }
        });
    }

    private void updateHeader() {
        android.widget.TextView textTotalProducts = findViewById(R.id.text_total_products);
        ListView listView = findViewById(R.id.list_cart);
        int count = adapter.getCount();
        textTotalProducts.setText(String.format("Giỏ hàng (%d)", count));
    }

    private void updateFooter() {
        android.widget.TextView textTotalPrice = findViewById(R.id.text_total_price);
        double total = 0.0;
        for (int i = 0; i < adapter.getCount(); i++) {
            CartItem item = (CartItem) adapter.getItem(i);
            if (item.isSelected()) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        textTotalPrice.setText(String.format("Tổng: %s", FormatData.formatCurrency(total)));
    }

    private void syncSelectAllCheckbox() {
        android.widget.CheckBox checkBoxSelectAll = findViewById(R.id.checkbox_select_all);
        boolean allSelected = true;
        boolean anySelected = false;
        for (int i = 0; i < adapter.getCount(); i++) {
            CartItem item = (CartItem) adapter.getItem(i);
            if (!item.isSelected()) allSelected = false;
            if (item.isSelected()) anySelected = true;
        }
        // Only update if changed, to avoid recursion
        if (allSelected && !checkBoxSelectAll.isChecked()) {
            checkBoxSelectAll.setChecked(true);
        } else if (!allSelected && checkBoxSelectAll.isChecked()) {
            checkBoxSelectAll.setChecked(false);
        }
    }
}