package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.auth.LoginRequest;
import com.hp.grocerystore.model.auth.LoginResponse;
import com.hp.grocerystore.model.base.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);
}
