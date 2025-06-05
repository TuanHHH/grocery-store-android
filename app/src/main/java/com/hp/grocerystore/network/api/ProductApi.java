package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.base.PaginationResponse;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.model.product.WishlistStatusResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApi {
    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") long productId);
    @GET("products")
    Call<ApiResponse<List<Product>>> getProducts(@Query("page") int page,
                                                 @Query("size") int size);

    @GET("products")
    Call<ApiResponse<PaginationResponse<Product>>> getProductsPaginated(
            @Query("page") int page,
            @Query("size") int size,
            @Query("filter") String filter
    );

    @GET("products/search")
    Call<ApiResponse<PaginationResponse<Product>>> searchProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("filter") String filter);

    @GET("products")
    Call<ApiResponse<PaginationResponse<Product>>> searchAndFilterProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("filter") String filter1,
            @Query("filter") String filter2,
            @Query("filter") String filter3,
            @Query("filter") String filter4,
            @Query("filter") String filter5,
            @Query("filter") String filter6,
            @Query("sort") String sort
    );

    @GET("wishlist/status/{id}")
    Call<ApiResponse<WishlistStatusResponse>> getWishlistStatus(@Path("id") long productId);

    @POST("generative/{id}")
    Call<String> getSummaryFeedback(@Path("id") long productId);
}
