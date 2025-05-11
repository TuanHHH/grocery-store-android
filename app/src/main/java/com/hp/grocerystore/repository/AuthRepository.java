package com.hp.grocerystore.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.model.auth.AuthResponse;
import com.hp.grocerystore.model.auth.LoginRequest;
import com.hp.grocerystore.model.auth.RegisterRequest;
import com.hp.grocerystore.model.auth.RegisterResponse;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.network.api.AuthApi;
import com.hp.grocerystore.utils.Resource;

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
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        loginResult.setValue(Resource.success(apiResponse.getData()));
                    } else {
                        loginResult.setValue(Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    loginResult.setValue(Resource.error("Đăng nhập thất bại"));
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
                    if (apiResponse.getStatusCode() == 200) {
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
