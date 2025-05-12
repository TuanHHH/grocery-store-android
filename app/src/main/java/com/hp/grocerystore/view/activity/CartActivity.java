package com.hp.grocerystore.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.utils.PreferenceManager;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.view.adapter.CartAdapter;
import com.hp.grocerystore.model.cart.CartItem;
import com.hp.grocerystore.R;
import com.hp.grocerystore.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private LinearLayout loginRequiredLayout;
    private Button buttonLoginRequired;
    private ImageView imageLoginIcon;
    private CartAdapter adapter;
    private List<CartItem> cartItems;
    private CartViewModel viewModel;
    private ListView listView;
    private TextView textEmptyCart;
    private Button buttonAddMoreProducts;

    private CheckBox checkboxSelectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cart), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupLoginCheck();
        setupListView();
        setupViewModel();
        setupSelectAllCheckbox();
    }

    private void initViews() {
        loginRequiredLayout = findViewById(R.id.login_required);
        buttonLoginRequired = findViewById(R.id.button_login_required);
        imageLoginIcon = findViewById(R.id.image_login_icon);
        listView = findViewById(R.id.list_cart);
        textEmptyCart = findViewById(R.id.text_empty_cart);
        buttonAddMoreProducts = findViewById(R.id.button_add_more_products);
        checkboxSelectAll = findViewById(R.id.checkbox_select_all);
        cartItems = new ArrayList<>();
    }

    private void setupLoginCheck() {
        PreferenceManager pref = new PreferenceManager(GRCApplication.getAppContext());
        boolean isLoggedIn = pref.isUserLoggedIn();
        if (!isLoggedIn) {
            loginRequiredLayout.setVisibility(View.VISIBLE);
            buttonLoginRequired.setVisibility(View.VISIBLE);
            imageLoginIcon.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            textEmptyCart.setVisibility(View.GONE);
            buttonAddMoreProducts.setVisibility(View.GONE);
        }
    }

    private void setupListView() {
        adapter = new CartAdapter(this, cartItems, new CartAdapter.CartActionListener() {
            @Override
            public void onIncreaseQuantity(int position) {
                CartItem item = cartItems.get(position);
                if (item.getQuantity() < item.getStock()) {
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
                checkEmptyCart();
            }

            @Override
            public void onSelectionChanged() {
                updateFooter();
                syncSelectAllCheckbox();
            }
        });
        listView.setAdapter(adapter);

        // Setup lazy loading
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() >= adapter.getCount() - 1) {
                        viewModel.loadMoreItems();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        viewModel.getCartItems().observe(this, this::handleCartItemsResponse);
    }

    private void handleCartItemsResponse(Resource<List<CartItem>> resource) {
        switch (resource.status) {
            case SUCCESS:
                if (resource.data != null) {
                    cartItems.clear();
                    cartItems.addAll(resource.data);
                    adapter.notifyDataSetChanged();
                    updateHeader();
                    updateFooter();
                    checkEmptyCart();
                    syncSelectAllCheckbox();
                }
                break;
            case ERROR:
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                break;
            case LOADING:
                // Handle loading state if needed
                break;
        }
    }

    private void checkEmptyCart() {
        if (cartItems.isEmpty()) {
            textEmptyCart.setVisibility(View.VISIBLE);
            buttonAddMoreProducts.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            textEmptyCart.setVisibility(View.GONE);
            buttonAddMoreProducts.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    public void navigateBack(View view) {
        finish();
    }

    public void processCheckout(View view) {
        double total = 0.0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        Toast.makeText(this, "Thanh toán: " + Extensions.formatCurrency(total), Toast.LENGTH_SHORT).show();
    }

    public void navigateToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("DefaultLocale")
    private void updateHeader() {
        TextView textTotalProducts = findViewById(R.id.text_total_products);
        int count = adapter.getCount();
        textTotalProducts.setText(String.format("Giỏ hàng (%d)", count));
    }

    private void updateFooter() {
        TextView textTotalPrice = findViewById(R.id.text_total_price);
        double total = 0.0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        textTotalPrice.setText(String.format("Tổng: %s", Extensions.formatCurrency(total)));
    }

    private void syncSelectAllCheckbox() {
        if (cartItems == null || cartItems.isEmpty()) {
            checkboxSelectAll.setChecked(false);
            return;
        }

        // Kiểm tra xem tất cả các sản phẩm còn hàng có được chọn không
        boolean allInStockSelected = true;
        boolean hasInStockItems = false;

        for (CartItem item : cartItems) {
            if (item.getStock() > 0) {
                hasInStockItems = true;
                if (!item.isSelected()) {
                    allInStockSelected = false;
                    break;
                }
            }
        }

        // Chỉ đánh dấu checkbox "Chọn tất cả" nếu có ít nhất một sản phẩm còn hàng
        checkboxSelectAll.setChecked(hasInStockItems && allInStockSelected);
    }

    private void setupSelectAllCheckbox() {
        CheckBox selectAllCheckbox = findViewById(R.id.checkbox_select_all);
        viewModel.getSelectAllState().observe(this, resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS) {
                selectAllCheckbox.setChecked(resource.data);
            }
        });
        selectAllCheckbox.setOnClickListener(v -> {
            boolean isChecked = selectAllCheckbox.isChecked();
            viewModel.selectAll(isChecked);
        });
    }

    public void onSelectAllClick(View view) {
        CheckBox selectAllCheckbox = findViewById(R.id.checkbox_select_all);
        boolean isChecked = selectAllCheckbox.isChecked();
        viewModel.selectAll(isChecked);
    }
}