package com.hp.grocerystore.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.auth.AuthResponse;
import com.hp.grocerystore.model.auth.LoginRequest;
import com.hp.grocerystore.model.auth.RegisterRequest;
import com.hp.grocerystore.model.auth.RegisterResponse;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.user.User;
import com.hp.grocerystore.network.api.AuthApi;
import com.hp.grocerystore.utils.AuthPreferenceManager;
import com.hp.grocerystore.utils.Resource;
import com.hp.grocerystore.utils.UserSession;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final AuthApi authApi;

    private final MutableLiveData<Resource<AuthResponse>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<RegisterResponse>> registerResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<User>> userInfoLiveData = new MutableLiveData<>();

    private final MutableLiveData<Resource<Void>> logoutLiveData = new MutableLiveData<>();

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
                        AuthPreferenceManager prefManager = AuthPreferenceManager.saveTokens(cookies, accessToken);
                        prefManager.saveUserData(loginData.getUser().getName(), loginData.getUser().getEmail());
                        loginResult.setValue(Resource.success(loginData));
                    } else {
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
                    String errorMessage = "Đăng ký thất bại";
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
                    registerResult.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RegisterResponse>> call, Throwable t) {
                registerResult.setValue(Resource.error(t.getMessage()));
            }
        });
        return registerResult;
    }

    public LiveData<Resource<User>> getUserInfo() {
        userInfoLiveData.setValue(Resource.loading());

        authApi.getUserInfo().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    UserSession.getInstance().setUser(user);
                    userInfoLiveData.setValue(Resource.success(user));
                } else {
                    userInfoLiveData.setValue(Resource.error("Không thể lấy thông tin user"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                userInfoLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return userInfoLiveData;
    }

    public LiveData<Resource<Void>> logout() {
        logoutLiveData.setValue(Resource.loading());
        AuthPreferenceManager pref = AuthPreferenceManager.getInstance(GRCApplication.getAppContext());
        String deviceHash = pref.getDevice();
        String cookieHeader = "device=" + deviceHash;
        authApi.logout(cookieHeader).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pref.clear();
                    UserSession.getInstance().clear();
                    logoutLiveData.setValue(Resource.success(null));
                } else {
                    logoutLiveData.setValue(Resource.error("Logout thất bại"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                logoutLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return logoutLiveData;
    }
}
