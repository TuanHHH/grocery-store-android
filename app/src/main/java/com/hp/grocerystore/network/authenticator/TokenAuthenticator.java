package com.hp.grocerystore.network.authenticator;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.auth.AuthResponse;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.receiver.SessionExpiredReceiver;
import com.hp.grocerystore.utils.Constants;
import com.hp.grocerystore.utils.AuthPreferenceManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Authenticator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    private static final String TAG = "TokenAuthenticator";
    private static final AtomicBoolean isRefreshing = new AtomicBoolean(false);
    private static String newAccessToken = null;

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (responseCount(response) > 3) {
            return null;
        }

        Log.d(TAG, "Authenticator triggered due to " + response.code());
        AuthPreferenceManager pref = AuthPreferenceManager.getInstance(GRCApplication.getAppContext());
        String refreshToken = pref.getRefreshToken();
        String device = pref.getDevice();

        if (refreshToken == null || device == null) {
            Log.d(TAG, "No refresh token or device found");
            notifySessionExpired(GRCApplication.getAppContext());
            return null;
        }

        // Nếu đang refresh token thì đợi (max 10s)
        if (isRefreshing.get()) {
            synchronized (isRefreshing) {
                try {
                    isRefreshing.wait(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Nếu có token mới thì sử dụng
            if (newAccessToken != null) {
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newAccessToken)
                        .build();
            }
            return null;
        }

        // Bắt đầu refresh token
        isRefreshing.set(true);
        newAccessToken = null;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        try {
            Request refreshRequest = new Request.Builder()
                    .url(Constants.BASE_URL_API + "auth/refresh")
                    .addHeader("Cookie", "refresh_token=" + refreshToken + "; device=" + device)
                    .post(RequestBody.create(Objects.requireNonNull(MediaType.parse("application/json")), ""))
                    .build();

            Response refreshResponse = client.newCall(refreshRequest).execute();
            Log.d(TAG, "Refresh response code: " + refreshResponse.code());

            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                String responseBody = refreshResponse.body().string();
                Log.d(TAG, "Refresh response body: " + responseBody);

                Gson gson = new Gson();
                Type type = new TypeToken<ApiResponse<AuthResponse>>() {}.getType();
                ApiResponse<AuthResponse> apiResponse = gson.fromJson(responseBody, type);
                if (apiResponse != null && apiResponse.getData() != null) {
                    String token = apiResponse.getData().getAccessToken();

                    if (token != null) {
                        Log.d(TAG, "New access token received");
                        newAccessToken = token;
                        List<String> cookies = refreshResponse.headers("Set-Cookie");
                        AuthPreferenceManager.saveTokens(cookies, token);
                        pref.saveUserData(apiResponse.getData().getUser().getName(), apiResponse.getData().getUser().getEmail());
                        // Thông báo cho các request khác biết đã có token mới
                        synchronized (isRefreshing) {
                            isRefreshing.notifyAll();
                        }

                        // Tạo request mới với token mới
                        return response.request().newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build();
                    }
                }
            } else {
                Log.e(TAG, "Refresh token request failed: " + refreshResponse.code());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during token refresh", e);
        } finally {
            isRefreshing.set(false);
            synchronized (isRefreshing) {
                isRefreshing.notifyAll();
            }
        }

        Log.d(TAG, "Token refresh failed, notifying session expired");
        notifySessionExpired(GRCApplication.getAppContext());
        return null;
    }

    private int responseCount(Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) count++;
        return count;
    }

    private void notifySessionExpired(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.hp.grocerystore.ACTION_SESSION_EXPIRED");
        intent.setClass(context, SessionExpiredReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.sendBroadcast(intent);
    }
}
