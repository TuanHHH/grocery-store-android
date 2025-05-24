package com.hp.grocerystore.network.api;

import com.google.gson.annotations.SerializedName;
import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.cart.CartItem;
import com.hp.grocerystore.model.order.CheckoutRequest;
import com.hp.grocerystore.model.order.Order;
import com.hp.grocerystore.model.product.ProductOrder;
import com.hp.grocerystore.utils.PagedResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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

//    @GET("orders/{id}/info")
//    Call<ApiResponse<PagedResult<Order>>> getOrderInfo(@Path("id") int orderId);
@GET("orders/{id}/info")
Call<ApiResponse<Order>> getOrderInfo(@Path("id") int orderId); // Sửa từ List<Order> thành Order


    @GET("orders/me")
    Call<ApiResponse<PagedResult<Order>>> getMyOrders(
            @Query("status") int status,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("orders/checkout")
    Call<ApiResponse> checkoutOrder(@Body CheckoutRequest request);

//    https://grocery-store-client-sepia.vercel.app/api/order/17/detail
//    @GET("order/{id}/detail")
//    Call<ApiResponse<PagedResult<ProductOrder>>> getOrderDetail(@Path("id") int orderId);
    @GET("order/{id}/detail")
    Call<ApiResponse<List<ProductOrder>>> getOrderDetail(@Path("id") int orderId);

}


//class CheckoutRequest {
//
//    @SerializedName("address")
//    private String address;
//
//    @SerializedName("phone")
//    private String phone;
//
//    @SerializedName("paymentMethod")
//    private String paymentMethod;
//
//    @SerializedName("totalPrice")
//    private double totalPrice;
//
//    @SerializedName("items")
//    private List<CartItem> items;
//
//    // Constructors, getters, setters
//    public CheckoutRequest(String address, String phone, String paymentMethod, double totalPrice, List<CartItem> items) {
//        this.address = address;
//        this.phone = phone;
//        this.paymentMethod = paymentMethod;
//        this.totalPrice = totalPrice;
//        this.items = items;
//    }
//
//    public String getAddress() { return address; }
//    public String getPhone() { return phone; }
//    public String getPaymentMethod() { return paymentMethod; }
//    public double getTotalPrice() { return totalPrice; }
//    public List<CartItem> getItems() { return items; }
//}
