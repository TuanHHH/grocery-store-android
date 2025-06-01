package com.hp.grocerystore.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.hp.grocerystore.R;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel viewModel;
    private TextInputEditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private FrameLayout loadingOverlay;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các view
        loadingOverlay = findViewById(R.id.loading_overlay);
        progressBar = findViewById(R.id.progress_bar);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    public void processRegister(View view) {
        String name = Extensions.getText(nameEditText);
        String email = Extensions.getText(emailEditText);
        String password = Extensions.getText(passwordEditText);
        String confirmPassword = Extensions.getText(confirmPasswordEditText);

        // Validate input
        if (name.isEmpty()) {
            nameEditText.setError("Vui lòng nhập tên");
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Vui lòng nhập email");
            return;
        }

        // Kiểm tra định dạng email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!email.matches(emailPattern)) {
            emailEditText.setError("Email không hợp lệ");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Vui lòng xác nhận mật khẩu");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Mật khẩu không khớp");
            return;
        }

        viewModel.register(name, email, password).observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case SUCCESS:
                    hideLoading();
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("email", email);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                case ERROR:
                    hideLoading();
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    break;
                case LOADING:
                    showLoading();
                    break;
            }
        });
    }

    public void navigateToLogin(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void navigateToHome(View view) {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
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