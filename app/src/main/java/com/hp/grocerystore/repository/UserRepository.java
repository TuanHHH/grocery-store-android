package com.hp.grocerystore.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.hp.grocerystore.application.GRCApplication;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.UploadFileResponse;
import com.hp.grocerystore.model.user.DeactivateOTP;
import com.hp.grocerystore.model.user.DeviceInfoResponse;
import com.hp.grocerystore.model.user.UpdatePasswordRequest;
import com.hp.grocerystore.model.user.UpdateUserRequest;
import com.hp.grocerystore.model.user.User;
import com.hp.grocerystore.network.api.UserApi;
import com.hp.grocerystore.utils.AuthPreferenceManager;
import com.hp.grocerystore.utils.Resource;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static volatile UserRepository INSTANCE;
    private final UserApi userApi;

    private UserRepository(UserApi userApi) {
        this.userApi = userApi;
    }

    public static UserRepository getInstance(UserApi userApi) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository(userApi);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<Void>> updatePassword(UpdatePasswordRequest request) {
        MutableLiveData<Resource<Void>> updatePasswordLiveData = new MutableLiveData<>();
        updatePasswordLiveData.setValue(Resource.loading());
        userApi.updatePassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        updatePasswordLiveData.setValue(Resource.success(null));
                    } else {
                        updatePasswordLiveData.setValue(Resource.error("Cập nhật mật khẩu thất bại"));
                    }
                } else {
                    String errorMessage = "Cập nhật mật khẩu thất bại";
                    try {
                        if (response.errorBody() != null) {
                            Gson gson = new Gson();
                            ApiResponse<?> errorResponse = gson.fromJson(response.errorBody().charStream(), ApiResponse.class);
                            if (errorResponse.getMessage() != null) {
                                errorMessage = errorResponse.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    updatePasswordLiveData.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                updatePasswordLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return updatePasswordLiveData;
    }


    public LiveData<Resource<UploadFileResponse>> uploadFile(MultipartBody.Part filePart, String folder) {
        MutableLiveData<Resource<UploadFileResponse>> uploadFileLiveData = new MutableLiveData<>();
        uploadFileLiveData.setValue(Resource.loading());

        RequestBody folderBody = RequestBody.create(okhttp3.MediaType.parse("text/plain"), folder);

        userApi.uploadFile(filePart, folderBody).enqueue(new Callback<ApiResponse<UploadFileResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UploadFileResponse>> call, @NonNull Response<ApiResponse<UploadFileResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UploadFileResponse data = response.body().getData();
                    uploadFileLiveData.setValue(Resource.success(data));
                } else {
                    uploadFileLiveData.setValue(Resource.error("Upload ảnh thất bại"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UploadFileResponse>> call, @NonNull Throwable t) {
                uploadFileLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return uploadFileLiveData;
    }


    public LiveData<Resource<User>> updateUser(UpdateUserRequest updateUserRequest) {
        MutableLiveData<Resource<User>> updateUserLiveData = new MutableLiveData<>();
        updateUserLiveData.setValue(Resource.loading());
        userApi.updateUser(updateUserRequest).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUserLiveData.setValue(Resource.success(response.body().getData()));
                } else {
                    String errorMessage = "Cập nhật thông tin thất bại";
                    try {
                        if (response.errorBody() != null) {
                            Gson gson = new Gson();
                            ApiResponse<?> errorResponse = gson.fromJson(response.errorBody().charStream(), ApiResponse.class);
                            if (errorResponse.getMessage() != null) {
                                errorMessage = errorResponse.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    updateUserLiveData.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                updateUserLiveData.setValue(Resource.error(t.getMessage()));
            }
        });

        return updateUserLiveData;
    }

    public LiveData<Resource<List<DeviceInfoResponse>>> getLoggerInDevice() {
        MutableLiveData<Resource<List<DeviceInfoResponse>>> loggedInDevicesLiveData = new MutableLiveData<>();
        loggedInDevicesLiveData.setValue(Resource.loading());
        AuthPreferenceManager pref = AuthPreferenceManager.getInstance(GRCApplication.getAppContext());
        String deviceHash = pref.getDevice();
        String cookieHeader = "device=" + deviceHash;
        userApi.getLoggedInDevices(cookieHeader).enqueue(new Callback<ApiResponse<List<DeviceInfoResponse>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<DeviceInfoResponse>>> call, @NonNull Response<ApiResponse<List<DeviceInfoResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loggedInDevicesLiveData.setValue(Resource.success(response.body().getData()));
                } else {
                    String errorMessage = "Lấy danh sách thiết bị thất bại";
                    try {
                        if (response.errorBody() != null) {
                            Gson gson = new Gson();
                            ApiResponse<?> errorResponse = gson.fromJson(response.errorBody().charStream(), ApiResponse.class);
                            if (errorResponse.getMessage() != null) {
                                errorMessage = errorResponse.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    loggedInDevicesLiveData.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<DeviceInfoResponse>>> call, @NonNull Throwable t) {
                loggedInDevicesLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return loggedInDevicesLiveData;
    }

    public LiveData<Resource<Void>> requestDeactivateAccount() {
        MutableLiveData<Resource<Void>> requestDeactivateLiveData = new MutableLiveData<>();
        requestDeactivateLiveData.setValue(Resource.loading());
        userApi.requestDeactivateAccount().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        requestDeactivateLiveData.setValue(Resource.success(null));
                    } else {
                        requestDeactivateLiveData.setValue(Resource.error("Yêu cầu thất bại"));
                    }
                } else {
                    String errorMessage = "Yêu cầu thất bại";
                    try {
                        if (response.errorBody() != null) {
                            Gson gson = new Gson();
                            ApiResponse<?> errorResponse = gson.fromJson(response.errorBody().charStream(), ApiResponse.class);
                            if (errorResponse.getMessage() != null) {
                                errorMessage = errorResponse.getMessage();
                            }
                        }
                    } catch (Exception e) {
                       // e.printStackTrace();
                    }
                    requestDeactivateLiveData.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                requestDeactivateLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return requestDeactivateLiveData;
    }

    public LiveData<Resource<Void>> confirmDeactivateAccount(DeactivateOTP otp) {
        MutableLiveData<Resource<Void>> confirmDeactivateLiveData = new MutableLiveData<>();
        confirmDeactivateLiveData.setValue(Resource.loading());
        userApi.confirmDeactivateAccount(otp).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.getStatusCode() == 200) {
                        AuthPreferenceManager pref = AuthPreferenceManager.getInstance(GRCApplication.getAppContext());
                        pref.clear();
                        confirmDeactivateLiveData.setValue(Resource.success(null));
                    } else {
                        confirmDeactivateLiveData.setValue(Resource.error("Yêu cầu thất bại"));
                    }
                } else {
                    String errorMessage = "Yêu cầu thất bại";
                    try {
                        if (response.errorBody() != null) {
                            Gson gson = new Gson();
                            ApiResponse<?> errorResponse = gson.fromJson(response.errorBody().charStream(), ApiResponse.class);
                            if (errorResponse.getMessage() != null) {
                                errorMessage = errorResponse.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    confirmDeactivateLiveData.setValue(Resource.error(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                confirmDeactivateLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
        return confirmDeactivateLiveData;
    }
}
