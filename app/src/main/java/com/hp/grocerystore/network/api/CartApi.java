package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.cart.CartItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CartApi {
    @GET("cart")
    Call<ApiResponse<PaginationResponse<CartItem>>> getCartItems(
        @Query("page") int page,
        @Query("limit") int limit
    );
}
