package com.hp.grocerystore.network.api;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.order.Order;
import com.hp.grocerystore.model.order.StatusUpdateRequest;
import com.hp.grocerystore.model.product.ProductOrder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApi {

    @GET("orders")
    Call<ApiResponse<List<Order>>> getOrders(
            @Query("status") int status,
            @Query("page") int page,
            @Query("size") int size
    );
    // Lấy chi tiết đơn hàng theo ID

    @GET("orders/{id}/info")
    Call<ApiResponse<Order>> getOrderInfo(@Path("id") int orderId);


    @GET("orders/me")
    Call<ApiResponse<PaginationResponse<Order>>> getMyOrders(
            @Query("status") int status,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("order/{id}/detail")
    Call<ApiResponse<List<ProductOrder>>> getOrderDetail(@Path("id") int orderId);

    @PATCH("orders/{id}/status")
    @Headers("Content-Type: application/json")
    Call<ApiResponse<Order>> updateOrderStatus(
            @Path("id") int orderId,
            @Body StatusUpdateRequest statusUpdate
    );
}

