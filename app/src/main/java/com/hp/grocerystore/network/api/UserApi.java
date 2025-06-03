package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.UploadFileResponse;
import com.hp.grocerystore.model.user.DeactivateOTP;
import com.hp.grocerystore.model.user.DeviceInfoResponse;
import com.hp.grocerystore.model.user.UpdatePasswordRequest;
import com.hp.grocerystore.model.user.UpdateUserRequest;
import com.hp.grocerystore.model.user.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserApi {
    @PATCH("users/password")
    Call<ApiResponse<Void>> updatePassword(@Body UpdatePasswordRequest request);

    @Multipart
    @POST("files")
    Call<ApiResponse<UploadFileResponse>> uploadFile(
            @Part MultipartBody.Part file,
            @Part("folder") RequestBody folder
    );

    @PUT("users/account")
    Call<ApiResponse<User>> updateUser(
            @Body UpdateUserRequest request
    );

    @GET("users/devices")
    Call<ApiResponse<List<DeviceInfoResponse>>> getLoggedInDevices(@Header("Cookie") String cookie);

    @POST("deactivate/request")
    Call<ApiResponse<Void>> requestDeactivateAccount();

    @POST("deactivate/confirm")
    Call<ApiResponse<Void>> confirmDeactivateAccount(@Body DeactivateOTP otp);
}
