package com.hp.grocerystore.network.api;

import com.hp.grocerystore.model.base.ApiResponse;
import com.hp.grocerystore.model.product.Product;
import com.hp.grocerystore.utils.PagedResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApi {
    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") long productId);

//    Call<ApiResponse<List<Product>>> getProducts(int currentPage, int i);
    @GET("products")
    Call<ApiResponse<List<Product>>> getProducts(@Query("page") int page,
                                                 @Query("size") int size);

    @GET("products")
    Call<ApiResponse<PagedResult<Product>>> getProductsPaginated(
            @Query("page") int page,
            @Query("size") int size,
            @Query("filter") String filter
    );
}
