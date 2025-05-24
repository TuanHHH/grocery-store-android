
package com.hp.grocerystore.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.grocerystore.R;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.view.adapter.OrderAdapter;
import com.hp.grocerystore.viewmodel.OrderViewModel;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    private OrderViewModel viewModel;
    private OrderAdapter orderAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;  // Để hiển thị loading khi lấy dữ liệu

    private int status = 0; // Set default status (Pending) for the orders

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        status = getIntent().getIntExtra("orderStatus", 0);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        // Khởi tạo RecyclerView và Adapter
        recyclerView = findViewById(R.id.orderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(new ArrayList<>(), order -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(orderAdapter);

        // Khởi tạo ProgressBar
        progressBar = findViewById(R.id.progressBar);  // Thêm ProgressBar vào layout

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Quan sát thay đổi trạng thái đơn hàng từ ViewModel
        viewModel.getOrdersByStatus(status).observe(this, resource -> {
            switch (resource.status) {
                case SUCCESS:
                    // Dữ liệu đã được tải thành công
                    progressBar.setVisibility(View.GONE); // Ẩn ProgressBar
                    if (resource.data != null && resource.data.size() > 0) {
                        orderAdapter.updateOrders(resource.data);
                    } else {
                        // Nếu không có dữ liệu, hiển thị thông báo
                        Log.d("So luong don hang", resource.data.size() + "");
                        Toast.makeText(this, "No orders found.", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case ERROR:
                    // Xử lý lỗi khi lấy dữ liệu
                    progressBar.setVisibility(View.GONE); // Ẩn ProgressBar
                    Toast.makeText(this, "Error loading orders: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;

                case LOADING:
                    // Hiển thị ProgressBar khi đang tải dữ liệu
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        });

        // Gọi hàm fetchOrdersByStatus từ ViewModel để lấy dữ liệu đơn hàng
//        viewModel.fetchOrdersByStatus(status); // Lấy đơn hàng với status = 1 (Pending)
    }

    // Hàm quay lại màn hình trước
    public void navigateBack(View view) {
        finish();
    }
}
