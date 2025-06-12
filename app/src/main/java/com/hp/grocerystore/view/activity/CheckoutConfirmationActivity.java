package com.hp.grocerystore.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.hp.grocerystore.model.cart.CartItem;
import com.hp.grocerystore.model.order.CheckoutRequest;
import com.hp.grocerystore.model.payment.VNPayResponse;
import com.hp.grocerystore.model.user.User;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.utils.LoadingUtil;
import com.hp.grocerystore.utils.UserSession;
import com.hp.grocerystore.view.adapter.ConfirmationAdapter;
import com.hp.grocerystore.viewmodel.OrderViewModel;
import com.hp.grocerystore.viewmodel.VNPayViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckoutConfirmationActivity extends AppCompatActivity {
    private TextView  textTotalPrice,textName;
    private EditText editAddress,textPhone;
    private RecyclerView recyclerViewItems;
    private Button buttonConfirm, buttonVNPay;
    private ImageButton buttonBack;
    private List<CartItem> selectedItems;
    private double totalPrice;
    private String address, phone, paymentMethod;
    private OrderViewModel orderViewModel;
    private VNPayViewModel vnPayViewModel;
    private static final int VNPAY_REQUEST_CODE = 1001;
    User user = UserSession.getInstance().getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout_confirmation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupViewModel();
        retrieveData();
        setupRecyclerView();
        displayOrderDetails();
        setupListeners();
    }

    private void initViews() {
        editAddress = findViewById(R.id.edit_text_address);
        textPhone = findViewById(R.id.text_phone);
        textName = findViewById(R.id.text_name);
        textTotalPrice = findViewById(R.id.text_total_price);
        recyclerViewItems = findViewById(R.id.recycler_view_items);
        buttonConfirm = findViewById(R.id.button_confirm);
        buttonVNPay = findViewById(R.id.button_vnpay);
        buttonBack = findViewById(R.id.button_back);
    }

    private void setupViewModel() {
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        vnPayViewModel = new ViewModelProvider(this).get(VNPayViewModel.class);
    }

    private void retrieveData() {
        selectedItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedItems");
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0);
    }

    private void setupRecyclerView() {
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        ConfirmationAdapter adapter = new ConfirmationAdapter(this, selectedItems);
        recyclerViewItems.setAdapter(adapter);
    }

    private void displayOrderDetails() {
        editAddress.setText(user.getAddress());
        textPhone.setText(user.getPhone());
        textName.setText(user.getName());
        textTotalPrice.setText("Tổng cộng: " + Extensions.formatCurrency(totalPrice));
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> finish());
        buttonConfirm.setOnClickListener(v -> processCheckout());
        buttonVNPay.setOnClickListener(v -> processVNPayPayment());
    }
    private boolean checkCondition() {
        String address = editAddress.getText().toString().trim();
        String phone = textPhone.getText().toString().trim();
        if (totalPrice <= 0) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_LONG).show();
            return false;
        }
        Log.d("CheckoutConfirmation", "Total Price: " + totalPrice);

        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.length() < 9) {
            Toast.makeText(this, "Số điện thoại phải có ít nhất 9 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!phone.matches("^\\+?[0-9]+$")) {
            Toast.makeText(this, "Số điện thoại chỉ được chứa số", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void processCheckout() {
        if (!checkCondition()) {
            return;
        }

        CheckoutRequest request = new CheckoutRequest();
        request.setAddress(editAddress.getText().toString());
        request.setPhone(textPhone.getText().toString());
        request.setPaymentMethod("COD");
        request.setTotalPrice((int) totalPrice);
        request.setItems(selectedItems);

        orderViewModel.checkout(request);
        observeCheckoutResult();
    }

    private void observeCheckoutResult() {
        orderViewModel.getCheckoutResult().observe(this, result -> {
            switch (result.status) {
                case LOADING:
                    LoadingUtil.showLoading(findViewById(R.id.loading_overlay), findViewById(R.id.progress_bar));
                    break;
                case SUCCESS:
                    LoadingUtil.hideLoading(findViewById(R.id.loading_overlay), findViewById(R.id.progress_bar));
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    setResult(RESULT_OK);
                    finish();
                    break;
                case ERROR:
                    LoadingUtil.hideLoading(findViewById(R.id.loading_overlay), findViewById(R.id.progress_bar));
                    Toast.makeText(this, "Lỗi đặt hàng: " + result.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void processVNPayPayment() {
        if (!checkCondition()) {
            return;
        }
        address = editAddress.getText().toString();
        phone = textPhone.getText().toString();
        // Tạo JSON đơn hàng
        JSONObject orderJson = new JSONObject();
        try {
            orderJson.put("address", address);
            orderJson.put("phone", phone);
            orderJson.put("totalPrice", (long) totalPrice);
            orderJson.put("paymentMethod", "VNPAY");

            JSONArray itemsArray = new JSONArray();
            for (CartItem item : selectedItems) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("productId", item.getId());
                itemJson.put("productName", item.getProductName());
                itemJson.put("quantity", item.getQuantity());
                itemJson.put("unitPrice", item.getPrice());
                itemsArray.put(itemJson);
            }
            orderJson.put("items", itemsArray);
        } catch (JSONException e) {
            Log.e("CheckoutConfirmation", "Error creating order JSON", e);
            Toast.makeText(this, "Lỗi tạo dữ liệu đơn hàng", Toast.LENGTH_LONG).show();
            return;
        }

        String orderData = Base64.encodeToString(orderJson.toString().getBytes(), Base64.DEFAULT);
        orderData = orderData.replace("\n", "").replace("\r", "");
        vnPayViewModel.createPayment((long) totalPrice, orderData);
        observeVNPayResult();
    }

    private void observeVNPayResult() {
        vnPayViewModel.getPaymentResponseLiveData().observe(this, result -> {
            switch (result.status) {
                case LOADING:
                    LoadingUtil.showLoading(findViewById(R.id.loading_overlay), findViewById(R.id.progress_bar));
                    break;
                case SUCCESS:
                    LoadingUtil.hideLoading(findViewById(R.id.loading_overlay), findViewById(R.id.progress_bar));
                    VNPayResponse vnPayResponse = result.data;
                    if (vnPayResponse != null) {
                        Log.d("CheckoutConfirmation", "VNPayResponse: statusCode=" + vnPayResponse.getStatusCode() +
                                ", error=" + vnPayResponse.getError() + ", message=" + vnPayResponse.getMessage());
                        if (vnPayResponse.getData() != null) {
                            Log.d("CheckoutConfirmation", "ResponseData: code=" + vnPayResponse.getData().getCode() +
                                    ", message=" + vnPayResponse.getData().getMessage() + ", paymentUrl=" + vnPayResponse.getData().getPaymentData().getPaymentUrl());
                            if ("ok".equals(vnPayResponse.getData().getPaymentData().getCode())) {
                                String paymentUrl = vnPayResponse.getData().getPaymentData().getPaymentUrl();
                                if (paymentUrl != null && !paymentUrl.isEmpty()) {
                                    Log.d("CheckoutConfirmation", "Payment URL: " + paymentUrl);
                                    Intent intent = new Intent(this, VnPayPaymentActivity.class);
                                    intent.putExtra("paymentUrl", paymentUrl);
                                    startActivityForResult(intent, VNPAY_REQUEST_CODE);
                                } else {
                                    Log.e("CheckoutConfirmation", "Payment URL is null or empty");
                                    Toast.makeText(this, "Không thể tạo URL thanh toán: URL rỗng", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.e("CheckoutConfirmation", "Invalid code: " + vnPayResponse.getData().getCode());
                                Toast.makeText(this, "Không thể tạo URL thanh toán: Mã không hợp lệ", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.e("CheckoutConfirmation", "ResponseData is null");
                            Toast.makeText(this, "Không thể tạo URL thanh toán: Dữ liệu phản hồi null", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e("CheckoutConfirmation", "VNPayResponse is null");
                        Toast.makeText(this, "Không thể tạo URL thanh toán: Phản hồi null", Toast.LENGTH_LONG).show();
                    }
                    break;
                case ERROR:
                    LoadingUtil.hideLoading(findViewById(R.id.loading_overlay), findViewById(R.id.progress_bar));
                    Log.e("CheckoutConfirmation", "API Error: " + result.message);
                    Toast.makeText(this, "Lỗi gọi API VNPay: " + result.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VNPAY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String responseCode = data.getStringExtra("vnp_ResponseCode");
                if ("00".equals(responseCode)) {
                    Toast.makeText(this, "Thanh toán VNPay thành công!", Toast.LENGTH_SHORT).show();
                    processCheckout();
                } else {
                    Toast.makeText(this, "Thanh toán VNPay thất bại", Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Thanh toán VNPay đã bị hủy", Toast.LENGTH_LONG).show();
            }
        }
    }
}