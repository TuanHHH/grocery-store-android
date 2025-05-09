package com.hp.grocerystore.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.auth.LoginRequest;
import com.hp.grocerystore.model.auth.LoginResponse;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.network.RetrofitClient;
import com.hp.grocerystore.utils.PreferenceManager;
import com.hp.grocerystore.utils.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final Context context;

    public AuthRepository() {
        this.context = GRCApplication.getAppContext();
    }
    public LiveData<Resource<LoginResponse>> login(String email, String password) {
        MutableLiveData<Resource<LoginResponse>> loginLiveData = new MutableLiveData<>();
        loginLiveData.setValue(Resource.loading());
        LoginRequest loginRequest = new LoginRequest(email, password);
        RetrofitClient.getAuthApi(context).login(loginRequest).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginData = response.body().getData();
                    String accessToken = loginData.getAccessToken();
                    List<String> cookies = response.headers().values("Set-Cookie");
                    Map<String, String> cookieMap = new HashMap<>();
                    for (String cookie : cookies) {
                        String[] parts = cookie.split(";", 2);
                        String[] kv = parts[0].split("=", 2);
                        if (kv.length == 2) {
                            cookieMap.put(kv[0].trim(), kv[1].trim());
                        }
                    }

                    String refreshToken = cookieMap.get("refresh_token");
                    String device = cookieMap.get("device");
                    PreferenceManager prefManager = new PreferenceManager(context);
                    prefManager.saveTokens(accessToken, refreshToken, device);
                    prefManager.saveUserData(loginData.getUser().getName(), loginData.getUser().getEmail());
                    loginLiveData.setValue(Resource.success(loginData));
                }  else {
                    String errorMessage = "Đăng nhập thất bại";
                    try {
                        if (response.errorBody() != null) {
                            Gson gson = new Gson();
                            ApiResponse<?> errorResponse = gson.fromJson(response.errorBody().charStream(), ApiResponse.class);
                            if (errorResponse.getMessage() != null) {
                                errorMessage = errorResponse.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loginLiveData.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                loginLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage()));
            }
        });

        return loginLiveData;
    }
}
