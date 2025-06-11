package com.hp.grocerystore.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

    private Button btnCancelOrder;

    private LinearLayout orderContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orderDetail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int originalPadding = (int) (16 * getResources().getDisplayMetrics().density);
            v.setPadding(
                    Math.max(systemBars.left, originalPadding),
                    Math.max(systemBars.top, originalPadding),
                    Math.max(systemBars.right, originalPadding),
                    Math.max(systemBars.bottom, originalPadding)
            );
            return insets;
        });
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
//            Toast.makeText(this, "orderId " + orderId, Toast.LENGTH_SHORT).show();
        }

        initViews();
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        observeOrderInfo();
        observeProductList();

        viewModel.getOrderById(orderId);
        viewModel.getProductLiveData(orderId);

        btnCancelOrder.setOnClickListener(v -> {
            viewModel.updateOrderStatus(orderId, 3);
            viewModel.refreshOrdersByStatus(0);
            setResult(RESULT_OK);
            finish();
        });
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
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
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
//                        Toast.makeText(this, "Order data is null", Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(this, "No products found in this order", Toast.LENGTH_SHORT).show();
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
        orderIdText.setText("Đơn hàng #" + order.getId());
        String statusText;
        int status = order.getStatus();

        switch (status) {
            case 0:
                statusText = "Chờ xác nhận";
                btnCancelOrder.setVisibility(View.VISIBLE);
                deliveryTimeText.setText("Thời gian giao hàng: " + formatOrderTime(order.getDeliveryTime()));
//                orderStatusText.setTextColor(Color.RED);
                break;
            case 1:
                statusText = "Đang giao";
                deliveryTimeText.setText("Thời gian giao hàng: " + formatOrderTime(order.getDeliveryTime()));
//                orderStatusText.setTextColor(Color.BLUE);
                break;
            case 2:
                statusText = "Thành công";
                deliveryTimeText.setText("Thời gian giao hàng: " + formatOrderTime(order.getDeliveryTime()));
//                orderStatusText.setTextColor(Color.GREEN);
                break;
            case 3:
                statusText = "Đã hủy";
                deliveryTimeText.setText("Thời gian hủy giao hàng: " + formatOrderTime(order.getDeliveryTime()));
//                orderStatusText.setTextColor(Color.GRAY);
                break;
            default:
                statusText = "Không xác định";
                deliveryTimeText.setText("Thời gian giao hàng: " + formatOrderTime(order.getDeliveryTime()));
//                orderStatusText.setTextColor(Color.BLACK);
                break;
        }

        orderStatusText.setText("Trạng thái: " + statusText);
        paymentMethodText.setText("Thanh toán: " + order.getPaymentMethod());
        orderTimeText.setText("Thời gian đặt hàng: " + formatOrderTime(order.getOrderTime()));
        addressText.setText("Địa chỉ: " + order.getAddress());
        phoneText.setText("Số điện thoại: " + order.getPhone());
        userNameText.setText("Khách hàng: " + order.getUserName());
        totalPriceText.setText("Tổng tiền: " + Extensions.formatCurrency(order.getTotalPrice()));
    }
    private String formatOrderTime(String orderTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = inputFormat.parse(orderTime);
            
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getDefault());

            return outputFormat.format(date);
        } catch (Exception e) {
            return orderTime;
        }
    }
}
