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

    @GET("payment/vn-pay-callback")
    Call<Void> paymentCallback(
            @Query("vnp_ResponseCode") String responseCode,
            @Query("vnp_TxnRef") String txnRef,
            @Query("vnp_Amount") String amount,
            @Query("vnp_OrderInfo") String orderInfo,
            @Query("vnp_BankCode") String bankCode,
            @Query("vnp_TransactionNo") String transactionNo,
            @Query("vnp_PayDate") String payDate,
            @Query("vnp_SecureHash") String secureHash
    );
}
