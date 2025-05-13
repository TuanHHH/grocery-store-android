package com.hp.grocerystore.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hp.grocerystore.R;
import com.hp.grocerystore.utils.PreferenceManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 1500;

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

        new Handler().postDelayed(() -> {
            PreferenceManager prefManager = new PreferenceManager(SplashActivity.this);
            boolean isLoggedIn = prefManager.isUserLoggedIn();

            if (isLoggedIn) {
                // fake access token to check refresh token api
//                prefManager.setAccessToken("fake-eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdWRvcmkxMDA4QGdtYWlsLmNvbSIsImV4cCI6MTc0NTI3MDA1MCwiaWF0IjoxNzQ1MjI2ODUwLCJ1c2VyIjp7ImlkIjozMSwiZW1haWwiOiJzdWRvcmkxMDA4QGdtYWlsLmNvbSIsIm5hbWUiOiJTdWRvcmkiLCJyb2xlIjp7ImlkIjoyLCJyb2xlTmFtZSI6IlVTRsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdfQ.h8pGHiiUVGlZ3DM9p5zqdt7agcE9yYoCU7q4lgJ3Mlxg2HI7BVP5nvpm3Ef2x0o-K8OA9NJxn-VQVVzqc_QHYQ");
//                startActivity(new Intent(SplashActivity.this, ProductDetailActivity.class));
                startActivity(new Intent(SplashActivity.this, ProductListActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish();
        }, SPLASH_DELAY);
    }
}