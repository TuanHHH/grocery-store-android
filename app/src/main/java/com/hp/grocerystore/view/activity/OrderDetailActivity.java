package com.hp.grocerystore.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.grocerystore.R;
import com.hp.grocerystore.model.order.Order;
import com.hp.grocerystore.model.product.ProductOrder;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.view.adapter.OrderDetailAdapter;
import com.hp.grocerystore.viewmodel.OrderViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView orderIdText, orderStatusText, orderTimeText, deliveryTimeText,
            paymentMethodText, addressText, phoneText, userNameText, totalPriceText;
    private RecyclerView productRecyclerView;
    private OrderDetailAdapter productAdapter;
    private ProgressBar progressBar;

    private OrderViewModel viewModel;
    private int orderId;

    private LinearLayout orderContentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            Toast.makeText(this, "orderId " + orderId, Toast.LENGTH_SHORT).show();
        }

        initViews();
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        observeOrderInfo();
        observeProductList();

        viewModel.getOrderById(orderId);
        viewModel.getProductLiveData(orderId);
    }

    private void initViews() {
        orderIdText = findViewById(R.id.orderId);
        orderStatusText = findViewById(R.id.orderStatus);
        orderTimeText = findViewById(R.id.orderTime);
        deliveryTimeText = findViewById(R.id.deliveryTime);
        paymentMethodText = findViewById(R.id.paymentMethod);
        addressText = findViewById(R.id.address);
        phoneText = findViewById(R.id.phone);
        userNameText = findViewById(R.id.userName);
        totalPriceText = findViewById(R.id.totalPrice);
        progressBar = findViewById(R.id.progressBar);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        orderContentLayout = findViewById(R.id.orderContentLayout);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new OrderDetailAdapter(new ArrayList<>());
        productRecyclerView.setAdapter(productAdapter);
    }

    private void observeOrderInfo() {
        viewModel.getOrderById(orderId).observe(this, resource -> {
            switch (resource.status) {
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    if (resource.data != null) {
                        orderContentLayout.setVisibility(View.VISIBLE);
                        bindOrderData(resource.data);
//                        Toast.makeText(this, "Loaded order successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Order data is null", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading order: " + resource.message, Toast.LENGTH_LONG).show();
                    break;
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    orderContentLayout.setVisibility(View.GONE);
                    break;
            }
        });
    }

    private void observeProductList() {
        viewModel.getProductLiveData(orderId).observe(this, resource -> {
            switch (resource.status) {
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    List<ProductOrder> productOrders = resource.data;
                    if (productOrders != null && !productOrders.isEmpty()) {
                        productAdapter.updateProductOrders(productOrders);
//                        Toast.makeText(this, "Loaded " + productOrders.size() + " products", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No products found in this order", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading products: " + resource.message, Toast.LENGTH_LONG).show();
                    break;
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void bindOrderData(Order order) {
        orderIdText.setText("Order #" + order.getId());
        String statusText;
        int status = order.getStatus();

        switch (status) {
            case 0:
                statusText = "Pending";
                orderStatusText.setTextColor(Color.RED);
                break;
            case 1:
                statusText = "Delivery";
                orderStatusText.setTextColor(Color.BLUE);
                break;
            case 2:
                statusText = "Success";
                orderStatusText.setTextColor(Color.GREEN);
                break;
            case 3:
                statusText = "Canceled";
                orderStatusText.setTextColor(Color.GRAY);
                break;
            default:
                statusText = "Unknown";
                orderStatusText.setTextColor(Color.BLACK);
                break;
        }
        orderStatusText.setText("Status: " + statusText);
//        orderTimeText.setText("Order Time: " + order.getOrderTime());

//        deliveryTimeText.setText("Delivery Time: " + order.getDeliveryTime());
        paymentMethodText.setText("Payment: " + order.getPaymentMethod());
        deliveryTimeText.setText("Delivery Time: " + formatOrderTime(order.getOrderTime()));
        orderTimeText.setText("Order Time: " + formatOrderTime(order.getOrderTime()));
        addressText.setText("Address: " + order.getAddress());
        phoneText.setText("Phone: " + order.getPhone());
        userNameText.setText("User: " + order.getUserName());
//        totalPriceText.setText(String.format("Total: $%.2f", order.getTotalPrice()));
        totalPriceText.setText(Extensions.formatCurrency( order.getTotalPrice()));
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
}
