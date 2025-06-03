package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.auth.ForgotPasswordRequest;
import com.hp.grocerystore.model.auth.LoginRequest;
import com.hp.grocerystore.model.auth.AuthResponse;
import com.hp.grocerystore.model.auth.OTPRequest;
import com.hp.grocerystore.model.auth.OTPResponse;
import com.hp.grocerystore.model.auth.RegisterRequest;
import com.hp.grocerystore.model.auth.RegisterResponse;
import com.hp.grocerystore.model.auth.ResetPasswordRequest;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.user.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {
    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest loginRequest);
    @POST("auth/register")
    Call<ApiResponse<RegisterResponse>> register(@Body RegisterRequest registerRequest);

    @GET("auth/me")
    Call<ApiResponse<User>> getUserInfo();

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout(@Header("Cookie") String cookie);

    @POST("auth/forgot")
    Call<ApiResponse<Void>> sendOTPForgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/otp/verify")
    Call<ApiResponse<OTPResponse>> verifyOTP(@Body OTPRequest request);

    @POST("auth/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Query("token") String token, @Body ResetPasswordRequest request);
}
