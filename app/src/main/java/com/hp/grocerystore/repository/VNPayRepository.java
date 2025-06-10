package com.hp.grocerystore.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hp.grocerystore.model.payment.VNPayResponse;
import com.hp.grocerystore.network.api.VNPayApi;
import com.hp.grocerystore.utils.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VNPayRepository {
    private static volatile VNPayRepository INSTANCE;
    private final VNPayApi vnPayApi;
    private final MutableLiveData<Resource<VNPayResponse>> paymentResponseLiveData;
    private final MutableLiveData<Resource<Void>> paymentCallbackLiveData;

    private VNPayRepository(VNPayApi vnPayApi) {
        this.vnPayApi = vnPayApi;
        this.paymentResponseLiveData = new MutableLiveData<>();
        this.paymentCallbackLiveData = new MutableLiveData<>();
    }

    public static VNPayRepository getInstance(VNPayApi vnPayApi) {
        if (INSTANCE == null) {
            synchronized (VNPayRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new VNPayRepository(vnPayApi);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<VNPayResponse>> getPaymentResponseLiveData() {
        return paymentResponseLiveData;
    }

    public LiveData<Resource<Void>> getPaymentCallbackLiveData() {
        return paymentCallbackLiveData;
    }

    public void createPayment(long amount, String orderData) {
        paymentResponseLiveData.setValue(Resource.loading(null));

        Log.d("VNPayRepository", "Amount: " + amount + ", Order Data: " + orderData);

        vnPayApi.createPayment(amount, orderData).enqueue(new Callback<VNPayResponse>() {
            @Override
            public void onResponse(Call<VNPayResponse> call, Response<VNPayResponse> response) {
                Log.d("VNPayRepository", "HTTP Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("VNPayRepository", "API Success: " + response.body().toString());
                    Log.d("VNPayRepository Url", "Payment URL: " + response.body().getData().getPaymentData().getPaymentUrl());
                    Log.d("VNPayRepository Code", "Code: " + response.body().getData().getPaymentData().getCode());
                    Log.d("VNPayRepository Message", "Message: " + response.body().getData().getPaymentData().getMessage());

                    // ✅ Set vào instance variable
                    paymentResponseLiveData.setValue(Resource.success(response.body()));
                } else {
                    String errorMsg = "Failed to create payment. HTTP Code: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ", Error: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("VNPayRepository", "Error parsing error body", e);
                    }
                    Log.e("VNPayRepository", errorMsg);

                    paymentResponseLiveData.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<VNPayResponse> call, Throwable t) {
                String errorMsg = "Failed to create payment: " + t.getMessage();
                Log.e("VNPayRepository", errorMsg, t);

                paymentResponseLiveData.setValue(Resource.error(errorMsg, null));
            }
        });
    }

    public void handlePaymentCallback(String responseCode, String txnRef, String amount,
                                      String orderInfo, String bankCode, String transactionNo,
                                      String payDate, String secureHash) {
        paymentCallbackLiveData.setValue(Resource.loading());
        vnPayApi.paymentCallback(responseCode, txnRef, amount, orderInfo, bankCode,
                transactionNo, payDate, secureHash).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    paymentCallbackLiveData.setValue(Resource.success(null));
                } else {
                    paymentCallbackLiveData.setValue(Resource.error("Payment callback failed"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                paymentCallbackLiveData.setValue(Resource.error(t.getMessage()));
            }
        });
    }
}
