package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.wishlist.Wishlist;
import com.hp.grocerystore.utils.PagedResult;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WishlistApi {
    @POST("wishlist")
    Call<ApiResponse<Void>> addWishlist(
            @Body RequestBody productRequest
    );

    @GET("wishlist")
    Call<ApiResponse<PagedResult<Wishlist>>> getProductsInWishlist(
            @Query("page") int page,
            @Query("size") int size
    );
    @DELETE("wishlist/{id}")
    Call<ApiResponse<Void>> deleteWishlist(
            @Path("id") Long id
    );
}
