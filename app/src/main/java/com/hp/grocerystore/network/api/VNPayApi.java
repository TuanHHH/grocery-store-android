package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.payment.VNPayResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface VNPayApi {
    @POST("payment/vn-pay")
    Call<VNPayResponse> createPayment(
            @Query("amount") long amount,
            @Query("orderData") String orderData
    );
}
