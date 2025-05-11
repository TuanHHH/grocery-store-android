package com.hp.grocerystore.network.interceptor;

import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.utils.PreferenceManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final PreferenceManager preferenceManager;

    public AuthInterceptor() {
        this.preferenceManager = new PreferenceManager(GRCApplication.getAppContext());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = preferenceManager.getAccessToken();

        if (token != null && !token.isEmpty()) {
            Request newRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }
        return chain.proceed(originalRequest);
    }
}
