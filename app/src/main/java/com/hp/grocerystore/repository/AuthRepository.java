package com.hp.grocerystore.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.auth.AuthResponse;
import com.hp.grocerystore.model.auth.ForgotPasswordRequest;
import com.hp.grocerystore.model.auth.LoginRequest;
import com.hp.grocerystore.model.auth.OTPRequest;
import com.hp.grocerystore.model.auth.OTPResponse;
import com.hp.grocerystore.model.auth.RegisterRequest;
import com.hp.grocerystore.model.auth.RegisterResponse;
import com.hp.grocerystore.model.auth.ResetPasswordRequest;
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
    private static volatile AuthRepository INSTANCE;
    private final AuthApi authApi;

    private AuthRepository(AuthApi authApi) {
        this.authApi = authApi;
    }

    public static AuthRepository getInstance(AuthApi authApi) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuthRepository(authApi);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        MutableLiveData<Resource<AuthResponse>> loginResult = new MutableLiveData<>();
        loginResult.setValue(Resource.loading());
        LoginRequest request = new LoginRequest(email, password);
        authApi.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Response<ApiResponse<AuthResponse>> response) {
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
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                loginResult.setValue(Resource.error(t.getMessage()));
            }
        });
        return loginResult;
    }

    public LiveData<Resource<RegisterResponse>> register(String name, String email, String password) {
        MutableLiveData<Resource<RegisterResponse>> registerResult = new MutableLiveData<>();
        registerResult.setValue(Resource.loading());
        RegisterRequest request = new RegisterRequest(name, email, password);
        authApi.register(request).enqueue(new Callback<ApiResponse<RegisterResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<RegisterResponse>> call, @NonNull Response<ApiResponse<RegisterResponse>> response) {
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
            public void onFailure(@NonNull Call<ApiResponse<RegisterResponse>> call, @NonNull Throwable t) {
                registerResult.setValue(Resource.error(t.getMessage()));
            }
        });
        return registerResult;
    }

    public LiveData<Resource<User>> getUserInfo() {
        MutableLiveData<Resource<User>> userInfoLiveData = new MutableLiveData<>();
        userInfoLiveData.setValue(Resource.loading());

        authApi.getUserInfo().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    UserSession.getInstance().setUser(user);
                    userInfoLiveData.setValue(Resource.success(user));
                } else {
                    userInfoLiveData.setValue(Resource.error("Không thể lấy thông tin user"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                userInfoLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return userInfoLiveData;
    }

    public LiveData<Resource<Void>> logout() {
        MutableLiveData<Resource<Void>> logoutLiveData = new MutableLiveData<>();
        logoutLiveData.setValue(Resource.loading());
        AuthPreferenceManager pref = AuthPreferenceManager.getInstance(GRCApplication.getAppContext());
        String deviceHash = pref.getDevice();
        String cookieHeader = "device=" + deviceHash;
        authApi.logout(cookieHeader).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pref.clear();
                    UserSession.getInstance().clear();
                    logoutLiveData.setValue(Resource.success(null));
                } else {
                    logoutLiveData.setValue(Resource.error("Logout thất bại"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                logoutLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return logoutLiveData;
    }

    public LiveData<Resource<Void>> sendOTPForgotPassword(ForgotPasswordRequest request) {
        MutableLiveData<Resource<Void>> forgotPasswordLiveData = new MutableLiveData<>();
        forgotPasswordLiveData.setValue(Resource.loading());
        authApi.sendOTPForgotPassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    forgotPasswordLiveData.setValue(Resource.success(null));
                } else {
                    String errorMessage = "Gửi OTP thất bại";
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
                    forgotPasswordLiveData.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                forgotPasswordLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return forgotPasswordLiveData;
    }

    public LiveData<Resource<OTPResponse>> verifyOTP(OTPRequest request) {
        MutableLiveData<Resource<OTPResponse>> verifyOTPLiveData = new MutableLiveData<>();
        verifyOTPLiveData.setValue(Resource.loading());
        authApi.verifyOTP(request).enqueue(new Callback<ApiResponse<OTPResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<OTPResponse>> call, @NonNull Response<ApiResponse<OTPResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    verifyOTPLiveData.setValue(Resource.success(response.body().getData()));
                } else {
                    String errorMessage = "Xác thực OTP thất bại";
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
                    verifyOTPLiveData.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<OTPResponse>> call, @NonNull Throwable t) {
                verifyOTPLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return verifyOTPLiveData;
    }

    public LiveData<Resource<Void>> resetPassword(String token, ResetPasswordRequest request) {
        MutableLiveData<Resource<Void>> resetPasswordLiveData = new MutableLiveData<>();
        resetPasswordLiveData.setValue(Resource.loading());
        authApi.resetPassword(token, request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resetPasswordLiveData.setValue(Resource.success(null));
                } else {
                    String errorMessage = "Đổi mật khẩu thất bại";
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
                    resetPasswordLiveData.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                resetPasswordLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return resetPasswordLiveData;
    }

}
