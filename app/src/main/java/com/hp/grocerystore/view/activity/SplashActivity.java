package com.hp.grocerystore.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hp.grocerystore.R;
import com.hp.grocerystore.model.user.User;
import com.hp.grocerystore.utils.AuthPreferenceManager;
import com.hp.grocerystore.utils.UserSession;
import com.hp.grocerystore.viewmodel.LoginViewModel;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 1500;
    private LoginViewModel loginViewModel;
    private AuthPreferenceManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefManager = AuthPreferenceManager.getInstance(this);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        new Handler().postDelayed(this::handleSplashLogic, SPLASH_DELAY);
       
    }

    private void handleSplashLogic() {
        if (prefManager.isUserLoggedIn()) {
            String token = prefManager.getAccessToken();
            if (token != null) {
                fetchUserInfo(token);
            } else {
                // fallback: no token
                navigateToLogin();
            }
        } else {
            navigateToLogin();
        }
    }

    private void fetchUserInfo(String token) {
        loginViewModel.getUserInfo().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case SUCCESS:
                    navigateToMain();
                    break;

                case ERROR:
                    Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                    break;

                case LOADING:
                    break;
            }
        });

        loginViewModel.getUserInfo();
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}