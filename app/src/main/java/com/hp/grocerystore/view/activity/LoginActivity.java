package com.hp.grocerystore.view.activity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hp.grocerystore.R;
import com.hp.grocerystore.utils.Extensions;
import com.hp.grocerystore.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleSignIn";
    private static final String client_id = "test.apps.googleusercontent.com";
    private LoginViewModel viewModel;
    MaterialButton loginButton, googleLoginButton;
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
        googleLoginButton = findViewById(R.id.googleLoginButton);
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

        googleLoginButton.setOnClickListener(view -> signInWithGoogle());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(client_id)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Lấy idToken
                String idToken = account.getIdToken();
                Toast.makeText(this, idToken, Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                Log.w(TAG, "Google sign-in failed", e);
            }
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
        // TODO: Implement forgot password
        Toast.makeText(this, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
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