package com.hp.grocerystore.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.CheckBox;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hp.grocerystore.model.user.User;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.utils.LoadingUtil;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.utils.UserSession;
import com.hp.grocerystore.view.adapter.CartAdapter;
import com.hp.grocerystore.model.cart.CartItem;
import com.hp.grocerystore.R;
import com.hp.grocerystore.viewmodel.CartViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private LinearLayout loginRequiredLayout;
    private Button buttonLoginRequired, buttonCheckout;
    private ImageView imageLoginIcon;
    private CartAdapter adapter;
    private List<CartItem> cartItems;
    private CartViewModel viewModel;
    private ListView listView;
    private TextView textEmptyCart;
    private CheckBox checkboxSelectAll;
    private FrameLayout loadingOverlay;
    private ProgressBar progressBar;
    private ConstraintLayout cartFooter;
    User user = UserSession.getInstance().getUser();
    private static final int CHECKOUT_REQUEST_CODE = 100;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refresh();
        }
    }

    private void initViews() {
        loadingOverlay = findViewById(R.id.loading_overlay);
        progressBar = findViewById(R.id.progress_bar);
        loginRequiredLayout = findViewById(R.id.login_required);
        buttonLoginRequired = findViewById(R.id.button_login_required);
        imageLoginIcon = findViewById(R.id.image_login_icon);
        listView = findViewById(R.id.list_cart);
        textEmptyCart = findViewById(R.id.text_empty_cart);
        checkboxSelectAll = findViewById(R.id.checkbox_select_all);
        buttonCheckout = findViewById(R.id.button_checkout);
        cartFooter = findViewById(R.id.footer_cart);
    }

    private void setupLoginCheck() {
        if (!Extensions.isLoggedIn(this)) {
            loginRequiredLayout.setVisibility(View.VISIBLE);
            buttonLoginRequired.setVisibility(View.VISIBLE);
            imageLoginIcon.setVisibility(View.VISIBLE);
            checkboxSelectAll.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            textEmptyCart.setVisibility(View.GONE);
            cartFooter.setVisibility(View.GONE);
            buttonLoginRequired.setOnClickListener(this::navigateToLogin);
        } else {
            cartItems = new ArrayList<>();
            setupListView();
            setupViewModel();
            setupSelectAllCheckbox();
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
                CartItem item = cartItems.get(position);
                viewModel.removeCartItem(item.getId());
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
        viewModel.getTotalItems().observe(this, this::handleTotalItemsResponse);
    }

    private void handleCartItemsResponse(Resource<List<CartItem>> resource) {
        switch (resource.status) {
            case SUCCESS:
                LoadingUtil.hideLoading(loadingOverlay, progressBar);
                if (resource.data != null) {
                    cartItems.clear();
                    cartItems.addAll(resource.data);
                    adapter.notifyDataSetChanged();
                    updateFooter();
                    checkEmptyCart();
                    syncSelectAllCheckbox();
                }
                break;
            case ERROR:
                LoadingUtil.hideLoading(loadingOverlay, progressBar);
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                break;
            case LOADING:
                LoadingUtil.showLoading(loadingOverlay, progressBar);
                break;
        }
    }

    private void handleTotalItemsResponse(Resource<Integer> resource) {
        if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
            updateHeader(resource.data);
        }
    }

    private void checkEmptyCart() {
        if (cartItems.isEmpty()) {
            textEmptyCart.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            checkboxSelectAll.setVisibility(View.GONE);
            buttonCheckout.setEnabled(false);
            buttonCheckout.setAlpha(0.5f);
        } else {
            textEmptyCart.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    public void navigateBack(View view) {
        finish();
    }

    @SuppressLint("DefaultLocale")
    public void processCheckout(View view) {
        double total = 0.0;
        int selectedCount = 0;
        StringBuilder selectedItemsLog = new StringBuilder();
        ArrayList<CartItem> selectedCartItems = new ArrayList<>();

        for (CartItem item : cartItems) {
            if (item.isSelected() && item.getStock() > 0) {
                total += item.getPrice() * item.getQuantity();
                selectedCount++;
                selectedItemsLog.append(String.format("\n- %s: %d x %s = %s",
                        item.getProductName(),
                        item.getQuantity(),
                        Extensions.formatCurrency(item.getPrice()),
                        Extensions.formatCurrency(item.getPrice() * item.getQuantity())
                ));
                selectedCartItems.add(item);
            }
        }

        String message = String.format("Thanh toán %d sản phẩm:%s\nTổng cộng: %s",
                selectedCount, selectedItemsLog, Extensions.formatCurrency(total));
        Log.d("CartActivity", "Chi tiết thanh toán: " + message);

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm hợp lệ để đặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCartItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user.getName().isEmpty() || user.getPhone().isEmpty() || user.getAddress().isEmpty()) {
            Toast.makeText(this, "Vui lòng cập nhật thông tin cá nhân trước khi đặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CheckoutConfirmationActivity.class);
        intent.putExtra("selectedItems", (Serializable) selectedCartItems);
        intent.putExtra("totalPrice", total);
        startActivityForResult(intent, CHECKOUT_REQUEST_CODE);
    }

    public void navigateToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("DefaultLocale")
    private void updateHeader(int totalItems) {
        TextView textTotalProducts = findViewById(R.id.text_total_products);
        textTotalProducts.setText(String.format("Giỏ hàng (%d)", totalItems));
    }

    @SuppressLint("DefaultLocale")
    private void updateFooter() {
        TextView textTotalPrice = findViewById(R.id.text_total_price);
        double total = 0.0;
        int selectedCount = 0;

        for (CartItem item : cartItems) {
            if (item.isSelected() && item.getStock() > 0) {
                total += item.getPrice() * item.getQuantity();
                selectedCount++;
            }
        }
        textTotalPrice.setText(String.format("Tổng: %s", Extensions.formatCurrency(total)));
    }

    private void syncSelectAllCheckbox() {
        if (cartItems == null || cartItems.isEmpty()) {
            checkboxSelectAll.setChecked(false);
            Log.d("CartActivity", "Giỏ hàng trống, bỏ chọn tất cả");
            return;
        }

        boolean allInStockSelected = true;
        boolean hasInStockItems = false;
        int inStockCount = 0;
        int selectedInStockCount = 0;

        for (CartItem item : cartItems) {
            if (item.getStock() > 0) {
                hasInStockItems = true;
                inStockCount++;
                if (item.isSelected()) {
                    selectedInStockCount++;
                } else {
                    allInStockSelected = false;
                }
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECKOUT_REQUEST_CODE && resultCode == RESULT_OK) {
            viewModel.refresh();
        }
    }
}