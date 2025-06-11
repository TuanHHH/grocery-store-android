package com.hp.grocerystore.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.grocerystore.R;
import com.hp.grocerystore.model.order.Order;
import com.hp.grocerystore.utils.AuthPreferenceManager;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.view.adapter.OrderAdapter;
import com.hp.grocerystore.viewmodel.OrderViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private OrderViewModel viewModel;
    private OrderAdapter orderAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout loginRequiredLayout;
    private Button buttonLoginRequired;
    private ImageView imageLoginIcon;

    private LiveData<Resource<List<Order>>> currentOrderLiveData;
    private ActivityResultLauncher<Intent> orderDetailLauncher;

    private int currentStatus = 0; // Default

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.order), (v, insets) -> {
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

        initViews();
        setupLoginCheck();

        currentStatus = getIntent().getIntExtra("orderStatus", 0);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // RecyclerView + Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(new ArrayList<>(), order -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            orderDetailLauncher.launch(intent); // Sử dụng launcher
        });
        recyclerView.setAdapter(orderAdapter);

        // Launcher để nhận kết quả khi quay về từ OrderDetailActivity
        orderDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Có thay đổi đơn hàng → refresh
                        viewModel.refreshOrdersByStatus(currentStatus);
                        loadOrders(currentStatus);
                    }
                });

        loadOrders(currentStatus);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.orderRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        loginRequiredLayout = findViewById(R.id.login_required);
        buttonLoginRequired = findViewById(R.id.button_login_required);
        imageLoginIcon = findViewById(R.id.image_login_icon);
    }

    private void setupLoginCheck() {
        AuthPreferenceManager pref = AuthPreferenceManager.getInstance(getApplicationContext());
        boolean isLoggedIn = pref.isUserLoggedIn();
        if (!isLoggedIn) {
            loginRequiredLayout.setVisibility(View.VISIBLE);
            buttonLoginRequired.setVisibility(View.VISIBLE);
            imageLoginIcon.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadOrders(int status) {
        if (currentOrderLiveData != null) {
            currentOrderLiveData.removeObservers(this);
        }

        currentOrderLiveData = viewModel.getOrdersByStatus(status);
        currentOrderLiveData.observe(this, this::handleOrderResponse);
    }

    private void handleOrderResponse(Resource<List<Order>> resource) {
        switch (resource.status) {
            case SUCCESS:
                progressBar.setVisibility(View.GONE);
                if (resource.data != null && !resource.data.isEmpty()) {
                    orderAdapter.updateOrders(resource.data);
                    Log.d("OrderActivity", "Loaded " + resource.data.size() + " orders for status " + currentStatus);
                } else {
                    orderAdapter.updateOrders(new ArrayList<>());
                    Toast.makeText(this, "Không có đơn hàng nào.", Toast.LENGTH_SHORT).show();
                }
                break;
            case ERROR:
                progressBar.setVisibility(View.GONE);
                Log.e("OrderActivity", "Lỗi tải đơn hàng: " + resource.message);
                Toast.makeText(this, "Lỗi tải đơn hàng: " + resource.message, Toast.LENGTH_SHORT).show();
                break;
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void navigateToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void navigateBack(View view) {
        finish();
    }

}
