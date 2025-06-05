package com.hp.grocerystore.network.interceptor;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.utils.AuthPreferenceManager;
import com.hp.grocerystore.utils.UserSession;
import com.hp.grocerystore.view.activity.LoginActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ForbiddenInterceptor implements Interceptor {
    private final AuthPreferenceManager preferenceManager;
    private final Gson gson;

    public ForbiddenInterceptor() {
        this.preferenceManager = AuthPreferenceManager.getInstance(GRCApplication.getAppContext());
        this.gson = new Gson();
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.code() == 403) {
            String responseBody = response.peekBody(Long.MAX_VALUE).string();
            try {
                ApiResponse<?> err = gson.fromJson(responseBody, ApiResponse.class);
                if (err != null && err.getStatusCode() == -11) {
                    if (preferenceManager.isUserLoggedIn() && UserSession.getInstance().isLoggedIn()) {
                        preferenceManager.clear();
                        Intent intent = new Intent(GRCApplication.getAppContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        GRCApplication.getAppContext().startActivity(intent);
                    }
                }
            } catch (Exception e) {
                // Optional: log error
            }
        }
        return response;
    }
}