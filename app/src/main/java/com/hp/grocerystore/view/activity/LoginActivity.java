package com.hp.grocerystore.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hp.grocerystore.R;
import com.hp.grocerystore.model.auth.LoginRequest;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.viewmodel.LoginViewModel;
import com.hp.grocerystore.viewmodel.ProductViewModel;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    MaterialButton loginButton, googleLoginButton;
    TextView registerText, forgotPasswordText, homeText;
    TextInputEditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.loginButton);
        googleLoginButton = findViewById(R.id.googleLoginButton);
        registerText = findViewById(R.id.registerText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        homeText = findViewById(R.id.homeText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Email không hợp lệ");
                }
            }
        });

        // Password validation
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
                if (password.isEmpty() || password.length() < 6) {
                    passwordEditText.setError("Mật khẩu tối thiểu 6 ký tự");
                }
            }
        });
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        homeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void processLogin(View view) {
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();

        boolean isValid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email không hợp lệ");
            isValid = false;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("Mật khẩu tối thiểu 6 ký tự");
            isValid = false;
        }

        if (!isValid) return;

        viewModel.login(email, password).observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case SUCCESS:
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                case ERROR:
                    String errorMessage = resource.message != null ? resource.message : "Đăng nhập thất bại. Vui lòng thử lại.";
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case LOADING:
                    // Optional: show progress dialog here
                    break;
            }
        });
    }
}