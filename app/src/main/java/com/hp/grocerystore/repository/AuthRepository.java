package com.hp.grocerystore.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.auth.AuthResponse;
import com.hp.grocerystore.model.auth.LoginRequest;
import com.hp.grocerystore.model.auth.RegisterRequest;
import com.hp.grocerystore.model.auth.RegisterResponse;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.network.api.AuthApi;
import com.hp.grocerystore.utils.PreferenceManager;
import com.hp.grocerystore.utils.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final AuthApi authApi;
    private final MutableLiveData<Resource<AuthResponse>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<RegisterResponse>> registerResult = new MutableLiveData<>();

    public AuthRepository(AuthApi authApi) {
        this.authApi = authApi;
    }

    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        loginResult.setValue(Resource.loading());
        LoginRequest request = new LoginRequest(email, password);
        authApi.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatusCode() == 200) {
                        AuthResponse loginData = response.body().getData();
                        String accessToken = loginData.getAccessToken();
                        List<String> cookies = response.headers().values("Set-Cookie");
                        PreferenceManager prefManager = PreferenceManager.saveTokens(cookies, accessToken);
                        prefManager.saveUserData(loginData.getUser().getName(), loginData.getUser().getEmail());
                        loginResult.setValue(Resource.success(loginData));
                    }
                    else {
                        loginResult.setValue(Resource.error(response.body().getMessage()));
                    }
                } else {
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
                    loginResult.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                loginResult.setValue(Resource.error(t.getMessage()));
            }
        });
        return loginResult;
    }

    public LiveData<Resource<RegisterResponse>> register(String name, String email, String password) {
        registerResult.setValue(Resource.loading());
        RegisterRequest request = new RegisterRequest(name, email, password);
        authApi.register(request).enqueue(new Callback<ApiResponse<RegisterResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<RegisterResponse>> call, Response<ApiResponse<RegisterResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<RegisterResponse> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 201) {
                        registerResult.setValue(Resource.success(apiResponse.getData()));
                    } else {
                        registerResult.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    registerResult.setValue(Resource.error("Đăng ký thất bại"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RegisterResponse>> call, Throwable t) {
                registerResult.setValue(Resource.error(t.getMessage()));
            }
        });
        return registerResult;
    }
}
