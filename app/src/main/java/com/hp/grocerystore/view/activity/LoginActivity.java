package com.hp.grocerystore.view.activity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hp.grocerystore.R;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.viewmodel.LoginViewModel;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    MaterialButton loginButton;
    TextView registerText, forgotPasswordText, homeText;
    TextInputEditText emailEditText, passwordEditText;

    private FrameLayout loadingOverlay;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadingOverlay = findViewById(R.id.loading_overlay);
        progressBar = findViewById(R.id.progress_bar);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        homeText = findViewById(R.id.homeText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            emailEditText.setText(email);
            passwordEditText.requestFocus();
        }
    }

    public void processLogin(View view) {
        String email = Extensions.getText(emailEditText);
        String password = Extensions.getText(passwordEditText);

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email không hợp lệ");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }

        viewModel.login(email, password).observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case SUCCESS:
                    hideLoading();
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    viewModel.getUserInfo();
                    break;
                case ERROR:
                    hideLoading();
                    String errorMessage = resource.message != null ? resource.message : "Đăng nhập thất bại. Vui lòng thử lại.";
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case LOADING:
                    showLoading();
                    break;
            }
        });
    }

    public void navigateToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void navigateToForgotPassword(View view) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgot_password);

        TextInputEditText etEmail = dialog.findViewById(R.id.email);
        Button btnSendOtp = dialog.findViewById(R.id.btnSendOtp);

        btnSendOtp.setOnClickListener(v -> {
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Vui lòng nhập email");
                etEmail.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Email không hợp lệ");
                etEmail.requestFocus();
                return;
            }

            viewModel.sendOTPForgotPassword(email).observe(this, resource ->{
                if (resource == null) return;

                switch (resource.status) {
                    case SUCCESS:
                        hideLoading();
                        Toast.makeText(this, "Đã gửi mã OTP tới: " + email, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        showOtpDialog(email);
                        break;
                    case ERROR:
                        hideLoading();
                        String errorMessage = resource.message != null ? resource.message : "Gửi OTP thất bại. Vui lòng thử lại.";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        showLoading();
                        break;
                }
            });
        });

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void showOtpDialog(String email) {
        Dialog otpDialog = new Dialog(this);
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpDialog.setContentView(R.layout.dialog_input_otp);

        TextInputEditText etOtp = otpDialog.findViewById(R.id.otp);
        Button btnVerifyOtp = otpDialog.findViewById(R.id.btnVerifyOtp);

        btnVerifyOtp.setOnClickListener(v -> {
            String otp = Objects.requireNonNull(etOtp.getText()).toString().trim();
            if (otp.isEmpty()) {
                etOtp.setError("Vui lòng nhập mã OTP");
                etOtp.requestFocus();
                return;
            }

            if (otp.length() != 6) {
                etOtp.setError("Mã OTP có 6 chữ số");
                etOtp.requestFocus();
                return;
            }

            viewModel.verifyOTP(email, otp).observe(this, resource ->{
                if (resource == null) return;

                switch (resource.status) {
                    case SUCCESS:
                        hideLoading();
                        Toast.makeText(this, "Xác thực thành công", Toast.LENGTH_SHORT).show();
                        otpDialog.dismiss();
                        break;
                    case ERROR:
                        hideLoading();
                        String errorMessage = resource.message != null ? resource.message : "Xác thực OTP thất bại. Vui lòng thử lại.";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        showLoading();
                        break;
                }
            });
//            Toast.makeText(this, "Đã xác nhận OTP: " + otp, Toast.LENGTH_SHORT).show();

        });

        otpDialog.show();
        Window window = otpDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
    public void navigateToHome(View view) {
        finish();
    }

    private void showLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    private void hideLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }
}