package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.cart.AddCartResponse;
import com.hp.grocerystore.model.cart.AddToCartRequest;
import com.hp.grocerystore.model.cart.CartItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CartApi {
    @GET("cart")
    Call<ApiResponse<PaginationResponse<CartItem>>> getCartItems(
        @Query("page") int page,
        @Query("size") int size
    );

    @DELETE("cart/{productId}")
    Call<ApiResponse<Void>> removeCartItem(@Path("productId") long productId);

    @POST("cart")
    Call<ApiResponse<AddCartResponse>> addOrUpdateCart(@Body AddToCartRequest request);
}
